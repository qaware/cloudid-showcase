package de.qaware.cloud.id.spire.impl;

import de.qaware.cloud.id.TestResources;
import de.qaware.cloud.id.spire.SocketChannelFactory;
import spire.api.workload.WorkloadOuterClass;
import spire.api.workload.WorkloadOuterClass.WorkloadEntry;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactorySpi;
import javax.net.ssl.ManagerFactoryParameters;
import java.security.*;
import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

public class SpiffeKeyManagerFactorySpi extends KeyManagerFactorySpi {

    private KeyManager keyManager;

    @Override
    protected void engineInit(KeyStore keyStore, char[] chars) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        init();
    }

    @Override
    protected void engineInit(ManagerFactoryParameters managerFactoryParameters) throws InvalidAlgorithmParameterException {
        init();
    }

    private void init() {
        // TODO: Make this work and remove the mock below
        // SocketChannelFactory channelFactory = new SocketChannelFactory("/tmp/test.sock");
        // WorkloadEntriesSupplier workloadEntriesSupplier = new WorkloadEntriesSupplier(channelFactory);
        Supplier<List<WorkloadEntry>> workloadEntriesSupplier = () -> TestResources.getTestBundles().getBundlesList();

        BundleSupplier bundleSupplier = new BundleSupplier(
                new BundlesSupplier(workloadEntriesSupplier),
                Duration.ofMinutes(15),
                Duration.ofMinutes(1));

        bundleSupplier.start();

        keyManager = new SpiffeKeyManager(bundleSupplier);
    }

    @Override
    protected KeyManager[] engineGetKeyManagers() {
        return new KeyManager[]{keyManager};
    }
}
