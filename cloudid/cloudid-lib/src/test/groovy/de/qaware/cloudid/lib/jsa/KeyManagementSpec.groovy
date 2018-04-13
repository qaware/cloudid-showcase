package de.qaware.cloudid.lib.jsa

import de.qaware.cloudid.lib.spire.CloudId
import de.qaware.cloudid.lib.spire.TestCloudIdManager
import spock.lang.Specification
import spock.util.environment.RestoreSystemProperties

import javax.net.ssl.X509KeyManager
import java.security.Principal
import java.time.Duration

import static de.qaware.cloudid.lib.jsa.SPIREProvider.ALIAS
import static de.qaware.cloudid.lib.spire.Config.CLOUD_ID_MANAGER_CLASS
import static de.qaware.cloudid.lib.spire.TestUtils.waitUntilBundleIsAvailable

/**
 * Specification testing key management with SPIFFE.
 */
@RestoreSystemProperties
class KeyManagementSpec extends Specification {

    static SPIREKeyManagerFactory keyManagerFactory

    def setupSpec() {
        System.setProperty(CLOUD_ID_MANAGER_CLASS.getSysProp(), TestCloudIdManager.class.getName())

        keyManagerFactory = new SPIREKeyManagerFactory()
    }

    def cleanupSpec() {
        CloudId.reset()
    }

    def 'get key managers'() {
        when:
        def keyManagers = keyManagerFactory.engineGetKeyManagers()

        then:
        keyManagers.length == 1
        keyManagers[0] instanceof X509KeyManager
    }

    def 'use key manager'() {
        when:
        def keyManager = (X509KeyManager) keyManagerFactory.engineGetKeyManagers()[0]
        waitUntilBundleIsAvailable(Duration.ofSeconds(5))

        then:
        keyManager.getPrivateKey(ALIAS) != null
        keyManager.getCertificateChain(ALIAS).length > 0
        keyManager.getClientAliases('', [] as Principal[]).toList() == [ALIAS]
    }

}
