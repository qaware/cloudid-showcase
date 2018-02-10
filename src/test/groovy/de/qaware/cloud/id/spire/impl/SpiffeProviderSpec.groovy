package de.qaware.cloud.id.spire.impl

import com.github.tomakehurst.wiremock.WireMockServer
import groovy.util.logging.Slf4j
import spock.lang.Specification

import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLSocketFactory
import java.time.Duration

import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.ok
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import static de.qaware.cloud.id.spire.impl.TestUtils.waitUntilBundleIsAvailable

@Slf4j
class SpiffeProviderSpec extends Specification {

    void setupSpec() {
        new SpiffeProvider().install(true)

        waitUntilBundleIsAvailable(Duration.ofSeconds(5))
    }

    void cleanupSpec() {
        new SpiffeProvider().uninstall()
    }

    def 'get key manager'() {
        when:
        def keyManagerFactory = KeyManagerFactory.getInstance(SpiffeProvider.ALGORITHM)

        then:
        keyManagerFactory.keyManagers.length == 1
        keyManagerFactory.keyManagers[0] instanceof SpiffeKeyManager
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
