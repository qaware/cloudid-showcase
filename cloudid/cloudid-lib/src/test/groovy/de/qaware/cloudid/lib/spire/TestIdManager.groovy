package de.qaware.cloudid.lib.spire

import de.qaware.cloudid.lib.Bundles
import de.qaware.cloudid.lib.IdManager
import de.qaware.cloudid.lib.TestResources

import java.util.function.Consumer

class TestIdManager implements IdManager {

    static final BundlesConverter bundlesConverter = new BundlesConverter()

    @Override
    void start() {

    }

    @Override
    void stop() {

    }

    @Override
    Bundles get() {
        return bundlesConverter.apply(TestResources.getTestBundles())
    }

    @Override
    void addListener(Consumer<Bundles> listener) {
        listener.accept(get())
    }

}