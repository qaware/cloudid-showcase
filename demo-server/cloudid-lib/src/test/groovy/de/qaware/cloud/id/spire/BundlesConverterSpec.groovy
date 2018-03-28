package de.qaware.cloud.id.spire

import de.qaware.cloud.id.TestResources
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

        // workloadEntry2 is valid until 07.03.2018 and workloadEntry until 22.01.2018, therefore workloadEntry2 should be the first bundle in the list
        bundlesResult.bundleList.get(0).notAfter > bundlesResult.bundleList.get(1).notAfter
        bundlesResult.bundleList.get(0).spiffeId == spiffeId + '2'
    }
}