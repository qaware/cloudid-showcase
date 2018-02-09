package de.qaware.cloud.id.spire.impl;

import de.qaware.cloud.id.spire.ChannelFactory;
import io.grpc.ManagedChannel;
import spire.api.workload.WorkloadGrpc;
import spire.api.workload.WorkloadOuterClass;

import java.util.List;
import java.util.function.Supplier;

/**
 * Fetches  SPIFFE workload bundles from the SPIRE agent.
 */
class WorkloadEntriesSupplier implements Supplier<List<WorkloadOuterClass.WorkloadEntry>> {

    private final ChannelFactory<?> channelFactory;

    /**
     * Initializes the bundle fetcher.
     *
     * @param channelFactory The channel factory used to connect to the SPIRE agent.
     */
    public WorkloadEntriesSupplier(ChannelFactory<?> channelFactory) {
        this.channelFactory = channelFactory;
    }

    /**
     * Fetches all bundles that are valid for the current workload.
     *
     * @return bundles
     */
    @Override
    public List<WorkloadOuterClass.WorkloadEntry> get() {
        ManagedChannel channel = channelFactory.createChannel().build();
        WorkloadGrpc.WorkloadBlockingStub workload = WorkloadGrpc.newBlockingStub(channel);
        WorkloadOuterClass.Empty request = WorkloadOuterClass.Empty.newBuilder().build();

        return workload.fetchAllBundles(request).getBundlesList();
    }


}
