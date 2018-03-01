package de.qaware.cloud.id.spire

import de.qaware.cloud.id.TestResources
import spock.lang.Specification

class BundleConverterSpec extends Specification {

    def 'convert'() {
        given:
        def bundles = TestResources.testBundles
        BundleConverter bundleConverter = new BundleConverter()

        when:
        SVIDBundle bundle = bundleConverter.convert(bundles.getBundles(0))

        then:
        bundle.svId == 'spiffe://salm.qaware.de/host/workload'
        bundle.certificate.subjectAlternativeNames.size() == 1
        bundle.keyPair.private != null
        // Sun JCE = EC
        // BouncyCastle = ECDSA
        bundle.keyPair.public.algorithm =~ /^EC(DSA)?$/
    }
}
