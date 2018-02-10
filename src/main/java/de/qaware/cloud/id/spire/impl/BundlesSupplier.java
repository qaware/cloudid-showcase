package de.qaware.cloud.id.spire.impl;

import de.qaware.cloud.id.spire.ChannelFactory;
import de.qaware.cloud.id.spire.SVIDBundle;
import io.grpc.ManagedChannel;
import lombok.RequiredArgsConstructor;
import spire.api.workload.WorkloadGrpc.WorkloadBlockingStub;
import spire.api.workload.WorkloadOuterClass.Empty;
import spire.api.workload.WorkloadOuterClass.WorkloadEntry;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;
import static spire.api.workload.WorkloadGrpc.newBlockingStub;

/**
 * Fetches  SPIFFE workload bundles from the SPIRE agent.
 */
@RequiredArgsConstructor
class BundlesSupplier implements Supplier<List<SVIDBundle>> {

    private static final Empty EMPTY_REQUEST = Empty.newBuilder().build();

    private final Function<WorkloadEntry, SVIDBundle> bundleConverter = new BundleConverter();

    private final ChannelFactory<?> channelFactory;

    /**
     * Fetches all bundles that are valid for the current workload.
     *
     * @return A list of {@link SVIDBundle}s which contains the certificates and the private key. The list may be empty.
     */
    @Override
    public List<SVIDBundle> get() {
        return getWorkloadEntries().stream()
                .map(bundleConverter)
                .collect(toList());

    }

    private List<WorkloadEntry> getWorkloadEntries() {
        ManagedChannel channel = channelFactory.createChannel().build();

        WorkloadBlockingStub workload = newBlockingStub(channel);

        return workload.fetchAllBundles(EMPTY_REQUEST)
                .getBundlesList();
    }

}
