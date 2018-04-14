package de.qaware.cloudid.lib.jsa

import de.qaware.cloudid.lib.spire.CloudId
import de.qaware.cloudid.lib.spire.TestCloudIdManager
import spock.lang.Specification
import spock.util.environment.RestoreSystemProperties

import javax.net.ssl.SSLEngine
import javax.net.ssl.X509ExtendedKeyManager
import javax.net.ssl.X509KeyManager
import java.security.Principal
import java.security.PrivateKey
import java.security.cert.X509Certificate
import java.time.Duration

import static de.qaware.cloudid.lib.jsa.SPIREProvider.ALIAS
import static de.qaware.cloudid.lib.spire.Config.ACL_DISABLED
import static de.qaware.cloudid.lib.spire.Config.CLOUD_ID_MANAGER_CLASS
import static de.qaware.cloudid.lib.spire.TestUtils.waitUntilBundleIsAvailable

/**
 * Specification testing key management with SPIFFE.
 */
@RestoreSystemProperties
class KeyManagementSpec extends Specification {

    static SPIREKeyManagerFactory keyManagerFactory

    def setupSpec() {
        System.setProperty(ACL_DISABLED.sysProp, true.toString())
        System.setProperty(CLOUD_ID_MANAGER_CLASS.getSysProp(), TestCloudIdManager.class.getName())
        getKeyType()

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
        def keyManager = (X509ExtendedKeyManager) keyManagerFactory.engineGetKeyManagers()[0]
        waitUntilBundleIsAvailable(Duration.ofSeconds(5))

        then:
        keyManager.getPrivateKey(ALIAS) == privateKey
        keyManager.getPrivateKey('no-such-alias') == null
        keyManager.getCertificateChain(ALIAS) == chain
        keyManager.getCertificateChain('no-such-alias') == null

        keyManager.getClientAliases(keyType, [] as Principal[]).toList() == [ALIAS]
        keyManager.getClientAliases('no-such-alias', [] as Principal[]).toList() == []
        keyManager.chooseClientAlias([keyType] as String[], [] as Principal[], Stub(Socket)) == ALIAS
        keyManager.chooseEngineClientAlias([keyType] as String[], [] as Principal[], Stub(SSLEngine)) == ALIAS

        keyManager.getServerAliases(keyType, [] as Principal[]).toList() == [ALIAS]
        keyManager.chooseServerAlias(keyType, [] as Principal[], Stub(Socket)) == ALIAS
        keyManager.chooseEngineServerAlias(keyType, [] as Principal[], Stub(SSLEngine)) == ALIAS
    }

    private static X509Certificate[] getChain() {
        CloudId.manager.singleBundle.caCertChainArray
    }

    private static PrivateKey getPrivateKey() {
        CloudId.manager.singleBundle.keyPair.private
    }

    private static String getKeyType() {
        privateKey.algorithm
    }

}
