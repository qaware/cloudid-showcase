package de.qaware.cloud.id.spire.impl;

import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactorySpi;
import javax.net.ssl.ManagerFactoryParameters;
import java.security.KeyStore;

@Slf4j
public class SpiffeKeyManagerFactory extends KeyManagerFactorySpi {

    private KeyManager keyManager;

    @Override
    protected void engineInit(KeyStore keyStore, char[] chars) {
        LOGGER.error("Delegating to a keystore backed key manager is not supported yet");
    }

    @Override
    protected void engineInit(ManagerFactoryParameters managerFactoryParameters) {
        LOGGER.error("Delegating to a keystore backed key manager is not supported yet");
    }

    private void init() {
        keyManager = new SpiffeKeyManager(BundleSupplierFactory.getInstance());
    }

    @Override
    protected KeyManager[] engineGetKeyManagers() {
        if (keyManager == null) {
            init();
        }

        return new KeyManager[]{keyManager};
    }

}
