package de.qaware.cloud.id;

import spire.api.workload.WorkloadOuterClass;

import java.io.IOException;

public class TestResources {

    public static WorkloadOuterClass.Bundles getTestBundles() {
        try {
            return WorkloadOuterClass.Bundles.parseFrom(TestResources.class.getResourceAsStream("/testBundles.grpc"));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
