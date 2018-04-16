package de.qaware.cloudid.lib.spire

import de.qaware.cloudid.lib.Bundles
import de.qaware.cloudid.lib.TestResources
import spire.api.workload.WorkloadOuterClass
import spock.lang.Specification

class BundlesConverterSpec extends Specification {

    def 'apply'() {
        given:
        def ttl = 10
        def spiffeId = 'spiffe://trust-domain/path'
        def testBundles = TestResources.testBundles

        def workloadEntry = testBundles.getBundles(0)
        def workloadEntry2 = WorkloadOuterClass.WorkloadEntry.newBuilder()
                .setSpiffeId(spiffeId + '2')
                // Certificate bytes
                .setSvid(TestResources.getSpireServerCert())
                // KeyPair
                .setSvidPrivateKey(TestResources.bundle0PrivateKey)
                // cert path
                .setSvidBundle(TestResources.getSpireServerCert())
                .build()
        def bundles = WorkloadOuterClass.Bundles.newBuilder()
                .addBundles(workloadEntry2)
                .addBundles(workloadEntry)
                .setTtl(ttl)
                .build()

        BundlesConverter bundlesConverter = new BundlesConverter()

        when:
        Bundles bundlesResult = bundlesConverter.apply(bundles)

        then:
        bundlesResult.expiry != null

        bundlesResult.bundleList.get(0) != null
        bundlesResult.bundleList.get(1) != null
    }

}