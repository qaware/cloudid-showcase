package de.qaware.cloud.id.spire.impl;

import de.qaware.cloud.id.TestResources;
import de.qaware.cloud.id.spire.BundleConverter;
import de.qaware.cloud.id.spire.SVIDBundle;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.BeforeClass;
import org.junit.Test;
import spire.api.workload.WorkloadOuterClass;

import java.security.Security;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Test case for {@link BundleConverter}.
 */
public class BundleConverterTest {

    @BeforeClass
    public static void setUp() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    public void testConvert() throws Exception {
        WorkloadOuterClass.Bundles bundles = TestResources.getTestBundles();
        BundleConverter bundleConverter = new BundleConverter();

        SVIDBundle bundle = bundleConverter.convert(bundles.getBundles(0));

        assertThat(bundle.getSvId(), is(equalTo("spiffe://example.org/host/workload")));
        assertThat(bundle.getCertificate().getSubjectAlternativeNames(), hasSize(1));
        assertThat(bundle.getKeyPair().getPrivate(), is(notNullValue()));
        assertThat(bundle.getKeyPair().getPrivate().getAlgorithm(), is(equalTo("ECDSA")));
    }
}