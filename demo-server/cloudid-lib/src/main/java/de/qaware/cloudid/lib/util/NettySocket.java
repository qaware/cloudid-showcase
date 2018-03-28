package de.qaware.cloudid.lib.util;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollDomainSocketChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerDomainSocketChannel;
import io.netty.channel.kqueue.KQueueDomainSocketChannel;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerDomainSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.unix.DomainSocketChannel;
import io.netty.channel.unix.ServerDomainSocketChannel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.SystemUtils;

import java.util.function.Supplier;

/**
 * Netty socket types.
 */
@RequiredArgsConstructor
public enum NettySocket {

    /**
     * EPoll sockets (Unix).
     */
    E_POLL(EpollEventLoopGroup::new, EpollDomainSocketChannel.class, EpollServerDomainSocketChannel.class),
    /**
     * KQueue sockets (Mac).
     */
    K_QUEUE(KQueueEventLoopGroup::new, KQueueDomainSocketChannel.class, KQueueServerDomainSocketChannel.class),
    /**
     * Java NIO sockets (Other systems, testing).
     *
     * No domain socket support.
     */
    NIO(NioEventLoopGroup::new, null, null);

    /**
     * Socket type supported by the current system.
     */
    public static final NettySocket CURRENT = getCurrent();

    private final Supplier<EventLoopGroup> eventLoopGroupSupplier;
    private final Class<? extends DomainSocketChannel> domainSocketChannelClass;
    private final Class<? extends ServerDomainSocketChannel> serverDomainSocketChannelClass;

    /**
     * Get the socket type supported by the system.
     *
     * @return socket type
     */
    private static NettySocket getCurrent() {
        if (SystemUtils.IS_OS_LINUX) {
            return E_POLL;
        } else if (SystemUtils.IS_OS_MAC) {
            return K_QUEUE;
        } else {
            return NIO;
        }
    }

    /**
     * Tells whether domain sockets are supported on the current system.
     *
     * @return whether domain sockets are supported on the current system
     */
    public boolean domainSocketsSupported() {
        return domainSocketChannelClass != null && serverDomainSocketChannelClass != null;
    }

    /**
     * Get a event loop group for the current system.
     * <p>
     * Please note that the life cycle of event loop groups needs to be managed.
     * See {@link EventLoopGroup} for more info.
     *
     * @return event loop group
     */
    public EventLoopGroup createEventLoopGroup() {
        return eventLoopGroupSupplier.get();
    }

    /**
     * Get the domain socket channel class for the current system.
     *
     * @return domain socket channel class
     */
    public Class<? extends DomainSocketChannel> getDomainSocketChannelClass() {
        checkDomainSocketsSupported();
        return domainSocketChannelClass;
    }

    /**
     * Get the server domain socket channel class for the current system.
     *
     * @return server domain socket channel class
     */
    public Class<? extends ServerDomainSocketChannel> getServerDomainSocketChannelClass() {
        checkDomainSocketsSupported();
        return serverDomainSocketChannelClass;
    }

    private void checkDomainSocketsSupported() {
        if (!domainSocketsSupported()) {
            throw new UnsupportedOperationException("Domain sockets are not supported on this system");
        }
    }

}
