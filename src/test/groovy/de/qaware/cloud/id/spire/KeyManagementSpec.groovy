package de.qaware.cloud.id.spire

import spock.lang.Specification
import spock.util.environment.RestoreSystemProperties

import javax.net.ssl.X509KeyManager
import java.security.Principal
import java.time.Duration

import static TestUtils.waitUntilBundleIsAvailable

/**
 * Specification testing key management with SPIFFE.
 */
@RestoreSystemProperties
class KeyManagementSpec extends Specification {

    static SPIREKeyManagerFactory keyManagerFactory

    def setupSpec() {
        System.setProperty('spire.bundlesSupplierClass', TestBundlesSupplier.class.getName())

        keyManagerFactory = new SPIREKeyManagerFactory()
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
