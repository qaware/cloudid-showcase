package de.qaware.cloudid.lib.spire

import de.qaware.cloudid.lib.TestResources
import de.qaware.cloudid.lib.WorkloadId
import spock.lang.Specification

class WorkloadIdConverterSpec extends Specification {

    def 'convert'() {
        given:
        def bundles = TestResources.testBundles
        WorkloadEntryConverter bundleConverter = new WorkloadEntryConverter()

        when:
        WorkloadId bundle = bundleConverter.convert(bundles.getBundles(0))

        then:
        bundle.spiffeId == 'spiffe://example.org/host/workload'
        bundle.svid.subjectAlternativeNames.size() == 1
        bundle.keyPair.private != null
        // Sun JCE = EC
        // BouncyCastle = ECDSA
        bundle.keyPair.public.algorithm =~ /^EC(DSA)?$/
    }
}
