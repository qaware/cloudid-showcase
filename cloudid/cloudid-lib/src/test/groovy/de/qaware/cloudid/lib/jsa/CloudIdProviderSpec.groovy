package de.qaware.cloudid.lib.jsa

import com.github.tomakehurst.wiremock.WireMockServer
import de.qaware.cloudid.lib.CloudId
import de.qaware.cloudid.lib.TestResources
import de.qaware.cloudid.lib.spire.DebugIdManager
import groovy.util.logging.Slf4j
import spock.lang.Specification
import spock.util.environment.RestoreSystemProperties

import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLSocketFactory
import java.security.KeyStore
import java.time.Duration

import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.ok
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import static de.qaware.cloudid.lib.Config.*
import static de.qaware.cloudid.lib.spire.TestUtils.waitUntilBundleIsAvailable

@Slf4j
@RestoreSystemProperties
class CloudIdProviderSpec extends Specification {

    void setupSpec() {
        System.setProperty(ID_MANAGER_CLASS.getSysProp(), DebugIdManager.class.getName())
        System.setProperty(DEBUG_KEYSTORE_LOCATION.getSysProp(), TestResources.testKeystoreLocation)
        System.setProperty(ACL_DISABLED.sysProp, true.toString())

        CloudIdProvider.install()
        CloudIdSocketFactory.install()

        waitUntilBundleIsAvailable(Duration.ofSeconds(5))
    }

    void cleanupSpec() {
        CloudIdProvider.uninstall()
        CloudIdSocketFactory.uninstall()
        CloudId.reset()
    }

    def 'get key manager'() {
        when:
        def keyManagerFactory = KeyManagerFactory.getInstance(CloudId.ALGORITHM)

        then:
        keyManagerFactory.keyManagers.length == 1
        keyManagerFactory.keyManagers[0] instanceof CloudIdKeyManager
    }

    def 'get default key manager'() {
        expect:
        KeyManagerFactory.getDefaultAlgorithm() == CloudId.ALGORITHM
    }

    def 'get key store'() {
        when:
        KeyStore keyStore = KeyStore.getInstance(CloudId.ALGORITHM)

        then:
        keyStore.type == CloudId.ALGORITHM
    }

    def 'get trust store'() {
        given:
        def type = "${CloudId.ALGORITHM}-TrustStore"

        when:
        KeyStore keyStore = KeyStore.getInstance(type)

        then:
        keyStore.type == type
    }

    def 'get default SSLSocketFactory'() {
        when:
        def socketFactory = SSLSocketFactory.getDefault()

        then:
        socketFactory instanceof CloudIdSocketFactory
    }


    def 'use for tls connection'() {
        setup:
        WireMockServer server = new WireMockServer(options()
                .bindAddress('localhost')
                .dynamicHttpsPort()
                .needClientAuth(true)
                .keystoreType(CloudId.ALGORITHM)
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
