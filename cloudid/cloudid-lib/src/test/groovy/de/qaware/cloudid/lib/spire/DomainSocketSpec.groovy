package de.qaware.cloudid.lib.spire

import de.qaware.cloudid.util.NettySocket
import io.grpc.Server
import io.grpc.netty.NettyServerBuilder
import io.netty.channel.EventLoopGroup
import io.netty.channel.unix.DomainSocketAddress
import spock.lang.IgnoreIf
import spock.lang.Specification

import java.nio.file.Paths

import static java.nio.file.Files.deleteIfExists
import static java.util.concurrent.TimeUnit.SECONDS

/**
 * Spec for the correct functioning of domain sockets on supported systems.
 */
@IgnoreIf({ !NettySocket.CURRENT.domainSocketsSupported() })
class DomainSocketSpec extends Specification {

    static final socketFile = "/tmp/${DomainSocketSpec.name}.sock"

    Server server
    EventLoopGroup serverGroup

    UdsBundlesSupplier bundlesSupplier

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

        bundlesSupplier = new UdsBundlesSupplier(socketFile)
    }

    void cleanup() {
        server.shutdownNow()
        serverGroup.shutdownGracefully().await(5, SECONDS)

        deleteIfExists(Paths.get(socketFile))
    }


    def 'domain socket works'() {
        expect:
        bundlesSupplier.get() != null
    }

}
