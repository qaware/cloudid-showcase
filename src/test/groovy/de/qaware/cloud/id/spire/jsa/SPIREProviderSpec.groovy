package de.qaware.cloud.id.spire.jsa

import com.github.tomakehurst.wiremock.WireMockServer
import de.qaware.cloud.id.spire.TestBundleSupplierFactory
import de.qaware.cloud.id.spire.TestUtils
import de.qaware.cloud.id.spire.jsa.SPIREKeyManager
import de.qaware.cloud.id.spire.jsa.SPIREProvider
import groovy.util.logging.Slf4j
import spock.lang.Specification
import spock.util.environment.RestoreSystemProperties

import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLSocketFactory
import java.time.Duration

import static TestUtils.waitUntilBundleIsAvailable
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.ok
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options

@Slf4j
@RestoreSystemProperties
class SPIREProviderSpec extends Specification {

    void setupSpec() {
        System.setProperty('spire.bundlesSupplierClass', TestBundleSupplierFactory.class.getName())

        new SPIREProvider().installAsDefault()

        waitUntilBundleIsAvailable(Duration.ofSeconds(5))
    }

    void cleanupSpec() {
        new SPIREProvider().uninstall()
    }

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
        socketFactory.class.simpleName != 'javax.net.ssl.DefaultSSLSocketFactory'
    }


    def 'use for tls connection'() {
        setup:
        WireMockServer server = new WireMockServer(options()
                .bindAddress('localhost')
                .dynamicHttpsPort()
                .needClientAuth(true)
                //.keystorePath(TestResources.wmKeystorePath)
                //.keystorePassword('useless-jetty')
                //.keystoreType('JKS'))
        )
        server.start()

        when:
        def body = 'all is well'
        server.stubFor(get('/').willReturn(ok(body)))

        and:
        def connection = new URL("https://localhost:${server.httpsPort()}/").openConnection()
        def responseBody = connection.inputStream.getText()

        then:
        responseBody == body

        cleanup:
        server.shutdown()
    }


}
