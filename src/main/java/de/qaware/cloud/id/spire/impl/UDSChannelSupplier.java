package de.qaware.cloud.id.spire.impl;

import io.grpc.Channel;
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

import java.util.function.Supplier;

/**
 * Supplier for UNIX domain socket channel.
 */
@RequiredArgsConstructor
class UDSChannelSupplier implements Supplier<Channel> {

    private final String socketFile;

    @Override
    public Channel get() {
        return NettyChannelBuilder.forAddress(new DomainSocketAddress(socketFile))
                .eventLoopGroup(getEventLoopGroup())
                .channelType(getServerSocketChannelClass())
                .usePlaintext(true)
                .build();
    }

    private static EventLoopGroup getEventLoopGroup() {
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

    private static Class<? extends ServerSocketChannel> getServerSocketChannelClass() {
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
