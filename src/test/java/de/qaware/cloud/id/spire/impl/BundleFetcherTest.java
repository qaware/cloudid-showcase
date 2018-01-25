package de.qaware.cloud.id.spire.impl;

import de.qaware.cloud.id.spire.SVIDBundle;
import de.qaware.cloud.id.spire.SocketChannelFactory;
import org.junit.Test;
import spire.api.workload.WorkloadOuterClass;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Unit test for the {@link BundleFetcher}.
 */
public class BundleFetcherTest {

    @Test
    public void testGetBundle() throws Exception {
        BundleFetcher bundleFetcher = new BundleFetcher(new SocketChannelFactory("/tmp/test.sock"));
        WorkloadOuterClass.Bundles bundles = WorkloadOuterClass.Bundles.parseFrom(getClass().getResourceAsStream("fetchAllBundles.grpc"));

        SVIDBundle bundle = bundleFetcher.getBundle(bundles.getBundles(0));
        assertThat(bundle.getSvId(), is(equalTo("spiffe://example.org/host/workload")));
        assertThat(bundle.getCertificate().getSubjectAlternativeNames(), hasSize(1));
        assertThat(bundle.getKeyPair().getPrivate(), is(notNullValue()));
        assertThat(bundle.getKeyPair().getPrivate().getAlgorithm(), is(equalTo("ECDSA")));
    }
}