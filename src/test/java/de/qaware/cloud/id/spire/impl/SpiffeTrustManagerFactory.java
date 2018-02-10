package de.qaware.cloud.id.spire.impl;

import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactorySpi;
import java.security.KeyStore;

import static de.qaware.cloud.id.spire.impl.BundleSupplierFactory.getBundleSupplier;

@Slf4j
public class SpiffeTrustManagerFactory extends TrustManagerFactorySpi {

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
        return new TrustManager[]{new SpiffeTrustManager(getBundleSupplier())};
    }
}
