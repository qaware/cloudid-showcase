package de.qaware.cloudid.lib.spire;

import spire.api.workload.WorkloadOuterClass;

import java.time.Instant;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

/**
 * Converts GRPC bundles into bundle data objects.
 */
class BundlesConverter implements Function<WorkloadOuterClass.Bundles, Bundles> {

    private static final Function<WorkloadOuterClass.WorkloadEntry, Bundle> BUNDLE_CONVERTER = new BundleConverter();

    @Override
    public Bundles apply(WorkloadOuterClass.Bundles bundles) {
        // Bundles are sorted descending by their expiry (newest first).
        List<Bundle> bundlesList = bundles.getBundlesList().stream()
                .map(BUNDLE_CONVERTER)
                .sorted((a, b) -> b.getNotAfter().compareTo(a.getNotAfter()))
                .collect(toList());

        return new Bundles(bundlesList, expiryOf(bundles));
    }

    private static Instant expiryOf(WorkloadOuterClass.Bundles bundles) {
        return Instant.now().plusSeconds(bundles.getTtl());
    }

}
