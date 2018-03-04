package de.qaware.cloud.id.spire;

import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactorySpi;
import java.security.KeyStore;

/**
 * SPIRE Trust manager factory.
 */
@Slf4j
public class SPIRETrustManagerFactory extends TrustManagerFactorySpi {

    @Override
    protected void engineInit(KeyStore keyStore) {
        LOGGER.error("Delegating to a keystore backed key manager is not supported yet");
    }

    @Override
    protected void engineInit(ManagerFactoryParameters managerFactoryParameters) {
        LOGGER.error("Delegating to a keystore backed key manager is not supported yet");
    }

    @Override
    protected TrustManager[] engineGetTrustManagers() {
        return new TrustManager[]{new SPIRETrustManager(StaticLauncher.getBundleSupplier())};
    }
}
