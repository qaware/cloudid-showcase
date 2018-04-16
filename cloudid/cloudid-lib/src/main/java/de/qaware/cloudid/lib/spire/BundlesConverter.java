package de.qaware.cloudid.lib.spire;

import de.qaware.cloudid.lib.Bundle;
import de.qaware.cloudid.lib.Bundles;
import spire.api.workload.WorkloadOuterClass;

import java.time.Instant;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

/**
 * Converts GRPC bundles into bundle data objects.
 */
class BundlesConverter implements Function<WorkloadOuterClass.Bundles, Bundles> {

    private static final Function<WorkloadOuterClass.WorkloadEntry, Bundle> BUNDLE_CONVERTER = new BundleConverter();

    @Override
    public Bundles apply(WorkloadOuterClass.Bundles bundles) {
        return new Bundles(
                bundles.getBundlesList().stream().map(BUNDLE_CONVERTER).collect(toList()),
                Instant.now().plusSeconds(bundles.getTtl()));
    }

}
