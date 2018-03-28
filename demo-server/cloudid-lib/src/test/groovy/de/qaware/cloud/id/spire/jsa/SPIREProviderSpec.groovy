package de.qaware.cloud.id.spire.jsa

import com.github.tomakehurst.wiremock.WireMockServer
import de.qaware.cloud.id.TestResources
import de.qaware.cloud.id.spire.DebugBundleSupplierFactory
import groovy.util.logging.Slf4j
import spock.lang.Ignore
import spock.lang.Specification
import spock.util.environment.RestoreSystemProperties

import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLSocketFactory
import java.time.Duration

import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.ok
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import static de.qaware.cloud.id.spire.Config.BUNDLE_SUPPLIER_FACTORY_CLASS
import static de.qaware.cloud.id.spire.TestUtils.waitUntilBundleIsAvailable

@Slf4j
@RestoreSystemProperties
class SPIREProviderSpec extends Specification {

    void setupSpec() {
        System.setProperty(BUNDLE_SUPPLIER_FACTORY_CLASS.getSysProp(), DebugBundleSupplierFactory.class.getName())
        System.setProperty(DebugBundleSupplierFactory.KEYSTORE_LOCATION.getSysProp(), TestResources.testKeystoreLocation)

        SPIREProvider.install()
        SPIRESocketFactory.install()

        waitUntilBundleIsAvailable(Duration.ofSeconds(5))
    }

    void cleanupSpec() {
        SPIREProvider.uninstall()
        SPIRESocketFactory.uninstall()
    }

    @Ignore("unclear if the key manager is actually required")
    def 'get key manager'() {
        when:
        def keyManagerFactory = KeyManagerFactory.getInstance('SPIRE')

        then:
        keyManagerFactory.keyManagers.length == 1
        keyManagerFactory.keyManagers[0] instanceof SPIREKeyManager
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
        server.shutdown()
    }


}
