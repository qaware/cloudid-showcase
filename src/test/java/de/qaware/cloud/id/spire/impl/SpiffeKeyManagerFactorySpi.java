package de.qaware.cloud.id.spire.impl;

import de.qaware.cloud.id.TestResources;
import de.qaware.cloud.id.spire.SVIDBundle;
import de.qaware.cloud.id.util.ExponentialBackoffSupplier;
import de.qaware.cloud.id.util.InterruptibleSupplier;
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

        InterruptibleSupplier<List<SVIDBundle>> bundlesSupplier = new ExponentialBackoffSupplier<>(
                new BundlesSupplier(workloadEntriesSupplier),
                2_000,
                60_000,
                1.5);

        BundleSupplier bundleSupplier = new BundleSupplier(
                bundlesSupplier,
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
