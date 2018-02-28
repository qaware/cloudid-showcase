package de.qaware.cloud.id.spire;

import de.qaware.cloud.id.util.NettySocket;
import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.unix.DomainSocketAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import spire.api.workload.WorkloadOuterClass;
import spire.api.workload.WorkloadOuterClass.WorkloadEntry;

import java.time.Instant;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.google.common.base.Verify.verify;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static spire.api.workload.WorkloadGrpc.newBlockingStub;

/**
 * Supplier for SPIRE workload bundles from the SPIRE agent.
 */
@Slf4j
@RequiredArgsConstructor
class BundlesSupplier implements Supplier<Bundles> {

    private static final Function<WorkloadEntry, SVIDBundle> BUNDLE_CONVERTER = new BundleConverter();

    private final EventLoopGroup eventLoopGroup = NettySocket.CURRENT.createEventLoopGroup();

    private final String socketFile;

    private static Instant getExpiry(WorkloadOuterClass.Bundles bundles) {
        // TODO: Verifiy that the TTL is indeed provided in seconds
        return Instant.now().plusSeconds(bundles.getTtl());
    }

    private static List<SVIDBundle> convert(WorkloadOuterClass.Bundles bundles) {
        List<SVIDBundle> bundlesList = bundles.getBundlesList().stream()
                .map(BUNDLE_CONVERTER)
                .collect(toList());

        verify(!bundlesList.isEmpty(),
                "Received 0 bundles. Is your workload registered with SPIRE?");

        // Verify the assumption that this workload has exactly one SPIFFE Id
        verify(bundlesList.stream()
                        .map(SVIDBundle::getSvId)
                        .collect(toSet()).size() == 1,
                "This workload must receive exactly one SPIFFE Id");

        return bundlesList;
    }

    private static List<SVIDBundle> sort(List<SVIDBundle> bundlesList) {
        bundlesList.sort((a, b) -> b.getNotAfter().compareTo(a.getNotAfter()));
        return bundlesList;
    }

    /**
     * Fetches all bundles that are valid for the current workload.
     *
     * @return bundles. The bundle list will be sorted descending by {@code notAfter}.
     */
    @Override
    public Bundles get() {
        WorkloadOuterClass.Bundles bundles = fetchBundles();

        LOGGER.debug("Received {} bundles with a TTL of {}s", bundles.getBundlesList().size(), bundles.getTtl());

        return new Bundles(sort(convert(bundles)), getExpiry(bundles));

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
