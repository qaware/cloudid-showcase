package de.qaware.cloudid.lib

import com.google.protobuf.ByteString

import static spire.api.workload.WorkloadOuterClass.Bundles

class TestResources {

    static testKeystoreLocation = 'classpath:spire_test_keystore_ec.jks'

    static Bundles getTestBundles() {
        return Bundles.parseFrom(TestResources.class.getResourceAsStream('/testBundles.grpc'))
    }

    static ByteString getSpireServerCert() {
        return ByteString.copyFrom(TestResources.class.getResourceAsStream('/certs/spire-server.der').bytes)
    }

    static ByteString getBundle0PrivateKey() {
        return ByteString.copyFrom(TestResources.class.getResourceAsStream('/certs/svid0-key.der').bytes)
    }


}
