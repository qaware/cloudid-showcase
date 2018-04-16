package de.qaware.cloudid.lib.spire;

import de.qaware.cloudid.util.NettySocket;
import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.unix.DomainSocketAddress;
import lombok.extern.slf4j.Slf4j;
import spire.api.workload.WorkloadOuterClass;

import java.util.function.Supplier;

import static com.google.common.base.Verify.verify;
import static spire.api.workload.WorkloadGrpc.newBlockingStub;

/**
 * Provides GRPC SPIRE bundles from a UNIX Domain Socket.
 */
@Slf4j
class UdsBundlesSupplier implements Supplier<WorkloadOuterClass.Bundles> {

    private final EventLoopGroup eventLoopGroup = NettySocket.CURRENT.createEventLoopGroup();

    private final String socketFile;

    /**
     * Constructor.
     *
     * @param socketFile file pointing to the UNIX Domain Socket the SPIRE agent is listening on.
     */
    UdsBundlesSupplier(String socketFile) {
        LOGGER.debug("Socket type: {}", NettySocket.CURRENT);

        if (!NettySocket.CURRENT.domainSocketsSupported()) {
            throw new IllegalStateException("No domain socket support on this system.");
        }

        this.socketFile = socketFile;
    }

    /**
     * Fetches all bundles that are valid for the current workload.
     *
     * @return bundles. The bundle list will be sorted descending by {@code notAfter}.
     */
    @Override
    public WorkloadOuterClass.Bundles get() {
        LOGGER.debug("Fetching bundles");
        WorkloadOuterClass.Bundles bundles = fetchBundles();

        verify(!bundles.getBundlesList().isEmpty(),
                "Received 0 bundles. Is your workload registered with SPIRE?");
        verify(bundles.getTtl() > 0,
                "Received bundles with and invalid TTL of %s", bundles.getTtl());
        LOGGER.debug("Received {} bundles with a TTL of {}s", bundles.getBundlesList().size(), bundles.getTtl());

        return bundles;

    }

    private WorkloadOuterClass.Bundles fetchBundles() {
        ManagedChannel channel = NettyChannelBuilder.forAddress(new DomainSocketAddress(socketFile))
                .eventLoopGroup(eventLoopGroup)
                .channelType(NettySocket.CURRENT.getDomainSocketChannelClass())
                .usePlaintext(true)
                .build();
        try {
            return newBlockingStub(channel).fetchAllBundles(newRequest());
        } finally {
            channel.shutdown();
        }
    }

    private static WorkloadOuterClass.Empty newRequest() {
        return WorkloadOuterClass.Empty.newBuilder().build();
    }

}
