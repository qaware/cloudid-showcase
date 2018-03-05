package de.qaware.cloud.id.spire.jsa;

import de.qaware.cloud.id.spire.StaticLauncher;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactorySpi;
import javax.net.ssl.ManagerFactoryParameters;
import java.security.KeyStore;

/**
 * SPIRE key manager factory
 */
@Slf4j
public class SPIREKeyManagerFactory extends KeyManagerFactorySpi {

    private KeyManager keyManager;

    @Override
    protected void engineInit(KeyStore keyStore, char[] chars) {
        SPIREKeyManagerFactory.LOGGER.error("Delegating to a keystore backed key manager is not supported yet");
    }

    @Override
    protected void engineInit(ManagerFactoryParameters managerFactoryParameters) {
        SPIREKeyManagerFactory.LOGGER.error("Delegating to a keystore backed key manager is not supported yet");
    }

    private void init() {
        keyManager = new SPIREKeyManager(StaticLauncher.getBundleSupplier());
    }

    @Override
    protected KeyManager[] engineGetKeyManagers() {
        if (keyManager == null) {
            init();
        }

        return new KeyManager[]{keyManager};
    }

}