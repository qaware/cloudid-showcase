package de.qaware.cloud.id.spire.impl

import spock.lang.Specification

import javax.net.ssl.X509KeyManager
import java.security.Principal
import java.time.Duration

import static de.qaware.cloud.id.spire.impl.TestUtils.waitUntilBundleIsAvailable

/**
 * Specification testing key management with SPIFFE.
 */
class KeyManagementSpec extends Specification {
    SpiffeKeyManagerFactory keyManagerFactory

    void setup() {
        keyManagerFactory = new SpiffeKeyManagerFactory()
    }

    def 'get key managers'() {
        when:
        def keyManagers = keyManagerFactory.engineGetKeyManagers()

        then:
        keyManagers.length == 1
        keyManagers[0] instanceof X509KeyManager
    }

    def 'use key manager'() {
        given:
        def svId = 'spiffe://example.org/host/workload'

        when:
        def keyManager = (X509KeyManager) keyManagerFactory.engineGetKeyManagers()[0]
        waitUntilBundleIsAvailable(Duration.ofSeconds(5))

        then:
        keyManager.getPrivateKey(svId) != null
        keyManager.getCertificateChain(svId).length > 0
        keyManager.getClientAliases('', [] as Principal[]).toList() == [svId]
    }

}
