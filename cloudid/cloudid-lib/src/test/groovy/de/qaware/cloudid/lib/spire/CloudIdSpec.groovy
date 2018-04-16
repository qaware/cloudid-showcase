package de.qaware.cloudid.lib.spire

import de.qaware.cloudid.lib.CloudId
import de.qaware.cloudid.lib.Config
import de.qaware.cloudid.util.NettySocket
import io.grpc.Server
import io.grpc.netty.NettyServerBuilder
import io.netty.channel.EventLoopGroup
import io.netty.channel.unix.DomainSocketAddress
import spock.lang.IgnoreIf
import spock.lang.Specification
import spock.util.environment.RestoreSystemProperties

import java.nio.file.Paths
import java.util.concurrent.CountDownLatch

import static java.nio.file.Files.deleteIfExists
import static java.util.concurrent.TimeUnit.SECONDS

/**
 * Specification for {@link de.qaware.cloudid.lib.CloudId}.
 */
@RestoreSystemProperties
@IgnoreIf({ !NettySocket.CURRENT.domainSocketsSupported() })
class CloudIdSpec extends Specification {

    static final socketFile = "/tmp/${CloudIdSpec.name}.sock"

    static Server server
    static EventLoopGroup serverGroup

    void setupSpec() {
        CloudId.reset()

        System.setProperty(Config.SPIRE_AGENT_SOCKET.sysProp, socketFile)

        deleteIfExists(Paths.get(socketFile))

        serverGroup = NettySocket.CURRENT.createEventLoopGroup()
        server = NettyServerBuilder.forAddress(new DomainSocketAddress(socketFile))
                .channelType(NettySocket.CURRENT.getServerDomainSocketChannelClass())
                .workerEventLoopGroup(serverGroup)
                .bossEventLoopGroup(serverGroup)
                .addService(new TestWorkloadService())
                .build()
        server.start()
    }

    void cleanupSpec() {
        server.shutdownNow()
        serverGroup.shutdownGracefully().await(5, SECONDS)

        deleteIfExists(Paths.get(socketFile))

        CloudId.reset()
    }

    def 'manager is created'() {
        expect:
        CloudId.idManager != null
    }

    def 'bundle is available'() {
        expect:
        CloudId.idManager.singleBundle != null
    }

    def 'callbacks work'() {
        given:
        def notifiedLatch = new CountDownLatch(1)

        when:
        CloudId.idManager.addListener({ b -> notifiedLatch.countDown() })

        then:
        notifiedLatch.await(5, SECONDS)
    }


}
