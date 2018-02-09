package de.qaware.cloud.id.spire;

import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.unix.DomainSocketAddress;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;

@RequiredArgsConstructor
public class SocketChannelFactory implements ChannelFactory<NettyChannelBuilder> {

    private final File socketFile;

    public SocketChannelFactory(String socketFile) {
        this(new File(socketFile));
    }

    @Override
    public ManagedChannelBuilder<NettyChannelBuilder> createChannel() {
        return NettyChannelBuilder.forAddress(new DomainSocketAddress(socketFile))
                .eventLoopGroup(getEventLoopGroup())
                .channelType(getServerSocketChannelClass())
                .usePlaintext(true);
    }

    private EventLoopGroup getEventLoopGroup() {
        switch (getCurrentSocketType()) {
            case E_POLL:
                return new EpollEventLoopGroup();
            case K_QUEUE:
                return new KQueueEventLoopGroup();
            case NIO: // fall through
            default:
                return new NioEventLoopGroup();
        }
    }

    private Class<? extends ServerSocketChannel> getServerSocketChannelClass() {
        switch (getCurrentSocketType()) {
            case E_POLL:
                return EpollServerSocketChannel.class;
            case K_QUEUE:
                return KQueueServerSocketChannel.class;
            case NIO: // fall through
            default:
                return NioServerSocketChannel.class;
        }
    }

    private static SocketType getCurrentSocketType() {
        if (SystemUtils.IS_OS_LINUX) {
            return SocketType.E_POLL;
        } else if (SystemUtils.IS_OS_MAC) {
            return SocketType.K_QUEUE;
        } else {
            return SocketType.NIO;
        }
    }

    private enum SocketType {
        E_POLL, K_QUEUE, NIO
    }
}
