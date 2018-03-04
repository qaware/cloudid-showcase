package de.qaware.cloud.id.spire;

import de.qaware.cloud.id.TestResources;

import java.util.function.Supplier;

public class TestBundleSupplierFactory implements BundleSupplierFactory {

    @Override
    public Supplier<Bundle> get() {
        return () -> TestResources.getTestBundles().getBundlesList().stream()
                .map(new BundleConverter())
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

}
