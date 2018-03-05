package de.qaware.cloud.id.spire

import de.qaware.cloud.id.util.NettySocket
import io.grpc.ManagedChannel
import io.grpc.Server
import io.grpc.netty.NettyChannelBuilder
import io.grpc.netty.NettyServerBuilder
import io.netty.channel.EventLoopGroup
import io.netty.channel.unix.DomainSocketAddress
import spire.api.workload.WorkloadOuterClass
import spock.lang.IgnoreIf
import spock.lang.Specification

import java.nio.file.Paths

import static java.nio.file.Files.deleteIfExists
import static java.util.concurrent.TimeUnit.SECONDS
import static spire.api.workload.WorkloadGrpc.newBlockingStub

/**
 * Spec for the correct functioning of domain sockets on supported systems.
 */
@IgnoreIf({ !NettySocket.CURRENT.domainSocketsSupported() })
class GrpcDomainSocketSpec extends Specification {

    static final socketFile = "/tmp/${GrpcDomainSocketSpec.class.getName()}.sock"

    Server server
    EventLoopGroup serverGroup

    ManagedChannel channel
    EventLoopGroup clientGroup

    void setup() {
        deleteIfExists(Paths.get(socketFile))

        serverGroup = NettySocket.CURRENT.createEventLoopGroup()
        server = NettyServerBuilder.forAddress(new DomainSocketAddress(socketFile))
                .channelType(NettySocket.CURRENT.getServerDomainSocketChannelClass())
                .workerEventLoopGroup(serverGroup)
                .bossEventLoopGroup(serverGroup)
                .addService(new TestWorkloadService())
                .build()
        server.start()

        clientGroup = NettySocket.CURRENT.createEventLoopGroup()
        channel = NettyChannelBuilder.forAddress(new DomainSocketAddress(socketFile))
                .eventLoopGroup(clientGroup)
                .channelType(NettySocket.CURRENT.getDomainSocketChannelClass())
                .usePlaintext(true)
                .build()
    }

    void cleanup() {
        server.shutdownNow()
        channel.shutdownNow()

        [serverGroup, clientGroup].each { it.shutdownGracefully().await(5, SECONDS) }

        deleteIfExists(Paths.get(socketFile))
    }


    def 'domain socket works'() {
        given:
        def request = WorkloadOuterClass.Empty.newBuilder().build()

        when:
        def bundles = newBlockingStub(channel).fetchAllBundles(request)

        then:
        bundles != null
    }

}
