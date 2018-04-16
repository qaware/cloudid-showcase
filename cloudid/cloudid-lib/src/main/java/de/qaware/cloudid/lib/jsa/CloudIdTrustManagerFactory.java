package de.qaware.cloudid.lib.jsa;

import de.qaware.cloudid.lib.CloudId;

import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactorySpi;
import java.security.KeyStore;

/**
 * CloudId Trust manager factory.
 */
public class CloudIdTrustManagerFactory extends TrustManagerFactorySpi {

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
        return new TrustManager[]{new CloudIdTrustManager(
                CloudId.getIdManager(),
                CloudId.getAclManager())};
    }

}
