package de.qaware.cloud.id.spire;

import io.grpc.Channel;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.net.ssl.SSLException;
import java.time.Duration;
import java.util.function.Supplier;

import static de.qaware.cloud.id.spire.TestUtils.waitUntilBundleIsAvailable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Unit test for the {@link RegistrationHandler}.
 */
@Ignore("Manual execution only")
public class RegistrationHandlerTest {

    private RegistrationHandler handler;

    @Before
    public void setUp() {
        // ChannelFactory<NettyChannelBuilder> serverChannelFactory = () -> NettyChannelBuilder.forAddress("192.168.99.100", 32610).usePlaintext(false);
        Supplier<Channel> channelSupplier = () -> {
            try {
                return NettyChannelBuilder
                        .forAddress("192.168.99.100", 30055)
                        .sslContext(GrpcSslContexts.forClient()
                                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                                .build())
                        .build();
            } catch (SSLException e) {
                throw new IllegalStateException(e);
            }
        };
        handler = new RegistrationHandler(channelSupplier);

        waitUntilBundleIsAvailable(Duration.ofSeconds(5));
    }

    @Test
    public void testFetchEntries() {
        handler.fetchEntries();
    }


    @Test
    public void testFetchBundle() {
//        ChannelFactory<?> socketChannelFactory = new UDSChannelSupplier("/Volumes/Cloud-ID/codebase/spire-k8s/socket/agent.sock");
//        ChannelFactory<?> serverChannelFactory = new TCPChannelFactory("192.168.99.100", 32610);
//        ChannelFactory<?> serverChannelFactory1 = new TCPChannelFactory("192.168.99.100", 30055);
//        ChannelFactory<?> channelFactory2 = new TCPChannelFactory("localhost", 32655);

        Supplier<Channel> proxyChannelFactory = () -> NettyChannelBuilder
                .forAddress("192.168.99.100", 31524)
                .usePlaintext(true)
                .build();

        Supplier<Bundle> bundleSupplier = StaticLauncher.getBundleSupplier();

        assertThat(bundleSupplier.get(), is(notNullValue()));
    }

}