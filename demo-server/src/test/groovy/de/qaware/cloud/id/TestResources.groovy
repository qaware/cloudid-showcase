package de.qaware.cloud.id

import com.google.protobuf.ByteString

import static spire.api.workload.WorkloadOuterClass.Bundles

class TestResources {

    static Bundles getTestBundles() {
        return Bundles.parseFrom(TestResources.class.getResourceAsStream('/testBundles.grpc'))
    }

    static ByteString getSpireServerCert() {
        return ByteString.copyFrom(TestResources.class.getResourceAsStream('/certs/spire-server.der').bytes)
    }

    static ByteString getBundle0PrivateKey() {
        return ByteString.copyFrom(TestResources.class.getResourceAsStream('/certs/svid0-key.der').bytes)
    }

    static getWmKeystorePath() {
        return TestResources.getResource('/wm-keystore.jks').getPath()
    }

}
