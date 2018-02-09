package de.qaware.cloud.id

import static spire.api.workload.WorkloadOuterClass.Bundles

class TestResources {

    static Bundles getTestBundles() {
        return Bundles.parseFrom(TestResources.class.getResourceAsStream("/testBundles.grpc"))
    }

}
