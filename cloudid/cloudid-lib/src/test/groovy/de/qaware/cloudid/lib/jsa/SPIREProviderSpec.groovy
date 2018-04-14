package de.qaware.cloudid.lib.jsa

import com.github.tomakehurst.wiremock.WireMockServer
import de.qaware.cloudid.lib.TestResources
import de.qaware.cloudid.lib.spire.CloudId
import de.qaware.cloudid.lib.spire.DebugCloudIdManager
import groovy.util.logging.Slf4j
import spock.lang.Specification
import spock.util.environment.RestoreSystemProperties

import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLSocketFactory
import java.time.Duration

import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.ok
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import static de.qaware.cloudid.lib.spire.Config.ACL_DISABLED
import static de.qaware.cloudid.lib.spire.Config.CLOUD_ID_MANAGER_CLASS
import static de.qaware.cloudid.lib.spire.DebugCloudIdManager.KEYSTORE_LOCATION
import static de.qaware.cloudid.lib.spire.TestUtils.waitUntilBundleIsAvailable

@Slf4j
@RestoreSystemProperties
class SPIREProviderSpec extends Specification {

    void setupSpec() {
        System.setProperty(CLOUD_ID_MANAGER_CLASS.getSysProp(), DebugCloudIdManager.class.getName())
        System.setProperty(KEYSTORE_LOCATION.getSysProp(), TestResources.testKeystoreLocation)
        System.setProperty(ACL_DISABLED.sysProp, true.toString())

        SPIREProvider.install()
        SPIRESocketFactory.install()

        waitUntilBundleIsAvailable(Duration.ofSeconds(5))
    }

    void cleanupSpec() {
        SPIREProvider.uninstall()
        SPIRESocketFactory.uninstall()
        CloudId.reset()
    }

    def 'get key manager'() {
        when:
        def keyManagerFactory = KeyManagerFactory.getInstance('SPIRE')

        then:
        keyManagerFactory.keyManagers.length == 1
        keyManagerFactory.keyManagers[0] instanceof SPIREKeyManager
    }

    def 'get default key manager'() {
        expect:
        KeyManagerFactory.getDefaultAlgorithm() == SPIREProvider.ALGORITHM
    }

    def 'get default SSLSocketFactory'() {
        when:
        def socketFactory = SSLSocketFactory.getDefault()

        then:
        socketFactory instanceof SPIRESocketFactory
    }


    def 'use for tls connection'() {
        setup:
        WireMockServer server = new WireMockServer(options()
                .bindAddress('localhost')
                .dynamicHttpsPort()
                .needClientAuth(true)
                .keystoreType("SPIRE")
        )
        server.start()

        when:
        def body = 'all is well'
        server.stubFor(get('/').willReturn(ok(body)))

        and:
        def connection = (HttpsURLConnection) new URL("https://localhost:${server.httpsPort()}/").openConnection()

        then:
        connection.inputStream.getText() == body

        cleanup:
        if (server != null) server.shutdown()
    }


}
