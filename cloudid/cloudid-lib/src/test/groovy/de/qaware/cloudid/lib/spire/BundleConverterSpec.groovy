package de.qaware.cloudid.lib.spire

import de.qaware.cloudid.lib.Bundle
import de.qaware.cloudid.lib.TestResources
import spock.lang.Specification

class BundleConverterSpec extends Specification {

    def 'convert'() {
        given:
        def bundles = TestResources.testBundles
        BundleConverter bundleConverter = new BundleConverter()

        when:
        Bundle bundle = bundleConverter.convert(bundles.getBundles(0))

        then:
        bundle.spiffeId == 'spiffe://example.org/host/workload'
        bundle.certificate.subjectAlternativeNames.size() == 1
        bundle.keyPair.private != null
        // Sun JCE = EC
        // BouncyCastle = ECDSA
        bundle.keyPair.public.algorithm =~ /^EC(DSA)?$/
    }
}
