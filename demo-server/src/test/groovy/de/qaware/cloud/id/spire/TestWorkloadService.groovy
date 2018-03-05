package de.qaware.cloud.id.spire

import io.grpc.stub.StreamObserver
import spire.api.workload.WorkloadGrpc
import spire.api.workload.WorkloadOuterClass

/**
 * Workload service implementation for test.
 */
class TestWorkloadService extends WorkloadGrpc.WorkloadImplBase {
    @Override
    void fetchAllBundles(WorkloadOuterClass.Empty request, StreamObserver<WorkloadOuterClass.Bundles> responseObserver) {
        responseObserver.onNext(WorkloadOuterClass.Bundles.newBuilder().build())
        responseObserver.onCompleted()
    }
}
