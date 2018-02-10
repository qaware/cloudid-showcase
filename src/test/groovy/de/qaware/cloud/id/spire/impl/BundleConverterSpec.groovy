package de.qaware.cloud.id.spire.impl

import de.qaware.cloud.id.TestResources
import de.qaware.cloud.id.spire.SVIDBundle
import spock.lang.Specification

class BundleConverterSpec extends Specification {

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
        // Sun JCE = EC
        // BouncyCastle = ECDSA
        bundle.keyPair.public.algorithm =~ /^EC(DSA)?$/
    }
}
