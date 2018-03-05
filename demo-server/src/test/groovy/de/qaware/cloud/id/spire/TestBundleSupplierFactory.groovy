package de.qaware.cloud.id.spire

import de.qaware.cloud.id.TestResources

import java.util.function.Supplier

class TestBundleSupplierFactory implements BundleSupplierFactory {

    static final BundleConverter bundleConverter = new BundleConverter()

    @Override
    Supplier<Bundle> get() {
        return { bundleConverter.apply(TestResources.getTestBundles().getBundlesList().first()) }
    }

}