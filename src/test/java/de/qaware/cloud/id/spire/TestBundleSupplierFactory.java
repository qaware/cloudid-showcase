package de.qaware.cloud.id.spire;

import de.qaware.cloud.id.TestResources;

import java.time.Instant;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TestBundleSupplierFactory implements BundleSupplierFactory {

    @Override
    public Supplier<SVIDBundle> get() {
        return () -> TestResources.getTestBundles().getBundlesList().stream()
                .map(new BundleConverter())
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

}
