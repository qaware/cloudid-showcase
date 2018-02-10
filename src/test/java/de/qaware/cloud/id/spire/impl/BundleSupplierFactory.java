package de.qaware.cloud.id.spire.impl;

import de.qaware.cloud.id.TestResources;
import de.qaware.cloud.id.spire.SVIDBundle;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

class BundleSupplierFactory {

    private static final Duration FORCE_UPDATE_AFTER = Duration.ofMinutes(15);
    private static final Duration UPDATE_AHEAD = Duration.ofMinutes(1);

    private static BundleSupplier instance;

    public static synchronized BundleSupplier getInstance() {
        if (instance == null) {
            instance = create();
        }
        return instance;
    }

    private static BundleSupplier create() {
        // TODO: Make this work and remove the mock below
        // SocketChannelFactory channelFactory = new SocketChannelFactory("/tmp/test.sock");
        // WorkloadEntriesSupplier workloadEntriesSupplier = new WorkloadEntriesSupplier(channelFactory);

        Supplier<List<SVIDBundle>> bundlesSupplier = () ->
                TestResources.getTestBundles().getBundlesList().stream()
                        .map(new BundleConverter())
                        .collect(Collectors.toList());

        // TODO: Add exponential backoff when the socket connection works

        BundleSupplier bundleSupplier = new BundleSupplier(bundlesSupplier, FORCE_UPDATE_AFTER, UPDATE_AHEAD);
        bundleSupplier.start();

        return bundleSupplier;
    }

}
