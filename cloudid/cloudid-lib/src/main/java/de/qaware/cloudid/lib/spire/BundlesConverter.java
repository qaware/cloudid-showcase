package de.qaware.cloudid.lib.spire;

import de.qaware.cloudid.lib.WorkloadId;
import de.qaware.cloudid.lib.WorkloadIds;
import spire.api.workload.WorkloadOuterClass;

import java.time.Instant;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

/**
 * Converts GRPC bundles into workload Idss.
 */
class BundlesConverter implements Function<WorkloadOuterClass.Bundles, WorkloadIds> {

    private static final Function<WorkloadOuterClass.WorkloadEntry, WorkloadId> BUNDLE_CONVERTER = new WorkloadEntryConverter();

    @Override
    public WorkloadIds apply(WorkloadOuterClass.Bundles bundles) {
        return new WorkloadIds(
                bundles.getBundlesList().stream().map(BUNDLE_CONVERTER).collect(toList()),
                Instant.now().plusSeconds(bundles.getTtl()));
    }

}
