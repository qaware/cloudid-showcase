package de.qaware.cloud.id.spire.impl;

import de.qaware.cloud.id.TestResources;
import de.qaware.cloud.id.spire.Bundles;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;
import java.util.stream.Collectors;

class BundleSupplierFactory {

    private static final Duration FORCE_UPDATE_AFTER = Duration.ofMinutes(15);
    private static final Duration UPDATE_AHEAD = Duration.ofMinutes(1);

    private static BundleSupplier bundleSupplier;

    public static synchronized BundleSupplier getBundleSupplier() {
        if (bundleSupplier == null) {
            bundleSupplier = create();
        }
        return bundleSupplier;
    }

    private static BundleSupplier create() {
        // TODO: Make this work and remove the mock below
        // UDSChannelSupplier channelFactory = new UDSChannelSupplier("/tmp/test.sock");
        // WorkloadEntriesSupplier workloadEntriesSupplier = new WorkloadEntriesSupplier(channelFactory);

        Supplier<Bundles> bundlesSupplier = () ->
                new Bundles(
                        TestResources.getTestBundles().getBundlesList().stream()
                                .map(new BundleConverter())
                                .collect(Collectors.toList()),
                        Instant.MAX);

        // TODO: Add exponential backoff when the socket connection works

        BundleSupplier bundleSupplier = new BundleSupplier(bundlesSupplier, FORCE_UPDATE_AFTER, UPDATE_AHEAD);
        bundleSupplier.start();

        return bundleSupplier;
    }

}
