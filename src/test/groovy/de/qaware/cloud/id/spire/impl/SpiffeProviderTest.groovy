package de.qaware.cloud.id.spire.impl

import spock.lang.Specification

import javax.net.ssl.KeyManagerFactory

class SpiffeProviderTest extends Specification {

    void setupSpec() {
        new SpiffeProvider().install(true)
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

}
