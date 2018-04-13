package de.qaware.cloudid.lib.spire

import de.qaware.cloudid.lib.TestResources

import java.util.function.Consumer
import java.util.function.Supplier

class TestCloudIdManager implements CloudIdManager {

    static final BundlesConverter bundlesConverter = new BundlesConverter()

    @Override
    void start() {

    }

    @Override
    void stop() {

    }

    @Override
    Bundles getBundles() {
        return bundlesConverter.apply(TestResources.getTestBundles())
    }

    @Override
    void addListener(Consumer<Bundles> listener) {

    }

}