package de.qaware.cloudid.lib.jsa;

import de.qaware.cloudid.lib.spire.CloudId;

import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactorySpi;
import java.security.KeyStore;

/**
 * SPIRE Trust manager factory.
 */
public class SPIRETrustManagerFactory extends TrustManagerFactorySpi {

    @Override
    protected void engineInit(KeyStore keyStore) {
        // No initialization required
    }

    @Override
    protected void engineInit(ManagerFactoryParameters managerFactoryParameters) {
        // No initialization required
    }

    @Override
    protected TrustManager[] engineGetTrustManagers() {
        return new TrustManager[]{new SPIRETrustManager(CloudId.getManager())};
    }

}
