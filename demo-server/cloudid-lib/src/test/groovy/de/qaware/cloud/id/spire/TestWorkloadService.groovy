package de.qaware.cloud.id.spire

import de.qaware.cloud.id.TestResources
import io.grpc.stub.StreamObserver
import spire.api.workload.WorkloadGrpc
import spire.api.workload.WorkloadOuterClass

/**
 * Workload service implementation for test.
 */
class TestWorkloadService extends WorkloadGrpc.WorkloadImplBase {

    @Override
    void fetchAllBundles(WorkloadOuterClass.Empty request, StreamObserver<WorkloadOuterClass.Bundles> responseObserver) {
        responseObserver.onNext(TestResources.testBundles)
        responseObserver.onCompleted()
    }

}
