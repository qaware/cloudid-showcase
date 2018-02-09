package de.qaware.cloud.id.spire.impl;

import de.qaware.cloud.id.spire.BundleConverter;
import de.qaware.cloud.id.spire.SVIDBundle;
import lombok.RequiredArgsConstructor;
import spire.api.workload.WorkloadOuterClass.WorkloadEntry;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;

/**
 * Fetches  SPIFFE workload bundles from the SPIRE agent.
 */
@RequiredArgsConstructor
class BundlesSupplier implements Supplier<List<SVIDBundle>> {

    private final Supplier<List<WorkloadEntry>> workloadEntrySupplier;
    private final Function<WorkloadEntry, SVIDBundle> bundleConverter = new BundleConverter();

    /**
     * Fetches all bundles that are valid for the current workload.
     *
     * @return A list of {@link SVIDBundle}s which contains the certificates and the private key. The list may be empty.
     */
    @Override
    public List<SVIDBundle> get() {
        return workloadEntrySupplier.get().stream()
                .map(bundleConverter)
                .collect(toList());

    }

}
