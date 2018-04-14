package de.qaware.cloudid.lib.jsa;

import de.qaware.cloudid.lib.spire.CloudId;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactorySpi;
import javax.net.ssl.ManagerFactoryParameters;
import java.security.KeyStore;

/**
 * CloudId key manager factory
 */
public class SPIREKeyManagerFactory extends KeyManagerFactorySpi {

    @Override
    protected void engineInit(KeyStore keyStore, char[] chars) {
        // No initialization required
    }

    @Override
    protected void engineInit(ManagerFactoryParameters managerFactoryParameters) {
        // No initialization required
    }

    @Override
    protected KeyManager[] engineGetKeyManagers() {
        return new KeyManager[]{new SPIREKeyManager(CloudId.getManager())};
    }

}
