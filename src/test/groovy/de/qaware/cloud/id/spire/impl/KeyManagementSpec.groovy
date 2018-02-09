package de.qaware.cloud.id.spire.impl

import spock.lang.Specification

import javax.net.ssl.X509KeyManager
import java.security.Principal

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
        def keyManager = (X509KeyManager) keyManagerFactory.engineGetKeyManagers()[0]

        when:
        // Wait for the updater thread to do it's work
        Thread.sleep(2_000)

        then:
        keyManager.getPrivateKey('') != null
        keyManager.getCertificateChain('').length > 0
        keyManager.getClientAliases('', [] as Principal[]).toList() == ['spiffe://example.org/host/workload']
    }

}
