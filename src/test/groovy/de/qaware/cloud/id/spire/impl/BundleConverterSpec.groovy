package de.qaware.cloud.id.spire.impl

import de.qaware.cloud.id.TestResources
import de.qaware.cloud.id.spire.BundleConverter
import de.qaware.cloud.id.spire.SVIDBundle
import org.bouncycastle.jce.provider.BouncyCastleProvider
import spock.lang.Specification

import java.security.Security

class BundleConverterSpec extends Specification {

    void setupSpec() {
        Security.addProvider(new BouncyCastleProvider())
    }

    def 'convert'() {
        given:
        def bundles = TestResources.testBundles
        BundleConverter bundleConverter = new BundleConverter()

        when:
        SVIDBundle bundle = bundleConverter.convert(bundles.getBundles(0))

        then:
        bundle.svId == 'spiffe://example.org/host/workload'
        bundle.certificate.subjectAlternativeNames.size() == 1
        bundle.keyPair.private != null
        bundle.keyPair.public.algorithm == 'ECDSA'
    }
}
