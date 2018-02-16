package de.qaware.cloud.id.spire;

import de.qaware.cloud.id.TestResources;

import java.time.Instant;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TestBundlesSupplier implements Supplier<Bundles> {

    @Override
    public Bundles get() {
        return new Bundles(
                TestResources.getTestBundles().getBundlesList().stream()
                        .map(new BundleConverter())
                        .collect(Collectors.toList()),
                Instant.MAX);
    }

}
