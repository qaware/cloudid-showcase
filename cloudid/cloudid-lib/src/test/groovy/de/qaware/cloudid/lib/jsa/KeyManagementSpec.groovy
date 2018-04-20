package de.qaware.cloudid.lib.jsa

import de.qaware.cloudid.lib.CloudId
import de.qaware.cloudid.lib.spire.TestIdManager
import spock.lang.Specification
import spock.util.environment.RestoreSystemProperties

import javax.net.ssl.SSLEngine
import javax.net.ssl.X509ExtendedKeyManager
import javax.net.ssl.X509KeyManager
import java.security.Principal
import java.security.PrivateKey
import java.security.cert.X509Certificate
import java.time.Duration

import static de.qaware.cloudid.lib.CloudId.SINGLE_ALIAS
import static de.qaware.cloudid.lib.Config.ACL_DISABLED
import static de.qaware.cloudid.lib.Config.ID_MANAGER_CLASS
import static de.qaware.cloudid.lib.spire.TestUtils.waitUntilBundleIsAvailable

/**
 * Specification testing key management with SPIFFE.
 */
@RestoreSystemProperties
class KeyManagementSpec extends Specification {

    static CloudIdKeyManagerFactory keyManagerFactory

    def setupSpec() {
        System.setProperty(ACL_DISABLED.sysProp, true.toString())
        System.setProperty(ID_MANAGER_CLASS.getSysProp(), TestIdManager.class.getName())
        getKeyType()

        keyManagerFactory = new CloudIdKeyManagerFactory()
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
        keyManager.getPrivateKey(SINGLE_ALIAS) == privateKey
        keyManager.getPrivateKey('no-such-alias') == null
        keyManager.getCertificateChain(SINGLE_ALIAS) == chain
        keyManager.getCertificateChain('no-such-alias') == null

        keyManager.getClientAliases(keyType, [] as Principal[]).toList() == [SINGLE_ALIAS]
        keyManager.getClientAliases('no-such-alias', [] as Principal[]).toList() == []
        keyManager.chooseClientAlias([keyType] as String[], [] as Principal[], Stub(Socket)) == SINGLE_ALIAS
        keyManager.chooseEngineClientAlias([keyType] as String[], [] as Principal[], Stub(SSLEngine)) == SINGLE_ALIAS

        keyManager.getServerAliases(keyType, [] as Principal[]).toList() == [SINGLE_ALIAS]
        keyManager.chooseServerAlias(keyType, [] as Principal[], Stub(Socket)) == SINGLE_ALIAS
        keyManager.chooseEngineServerAlias(keyType, [] as Principal[], Stub(SSLEngine)) == SINGLE_ALIAS
    }

    private static X509Certificate[] getChain() {
        CloudId.idManager.workloadId.caCertChainArray
    }

    private static PrivateKey getPrivateKey() {
        CloudId.idManager.workloadId.keyPair.private
    }

    private static String getKeyType() {
        privateKey.algorithm
    }

}
