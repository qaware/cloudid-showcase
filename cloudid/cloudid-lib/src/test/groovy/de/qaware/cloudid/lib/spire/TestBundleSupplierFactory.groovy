package de.qaware.cloudid.lib.spire

import de.qaware.cloudid.lib.TestResources

import java.util.function.Supplier

class TestBundleSupplierFactory implements BundleSupplierFactory {

    static final BundleConverter bundleConverter = new BundleConverter()

    @Override
    Supplier<Bundle> get() {
        return { bundleConverter.apply(TestResources.getTestBundles().getBundlesList().first()) }
    }

}