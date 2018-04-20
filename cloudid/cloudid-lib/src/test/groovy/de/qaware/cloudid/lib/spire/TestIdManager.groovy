package de.qaware.cloudid.lib.spire

import de.qaware.cloudid.lib.IdManager
import de.qaware.cloudid.lib.TestResources
import de.qaware.cloudid.lib.WorkloadIds

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
    WorkloadIds get() {
        return bundlesConverter.apply(TestResources.getTestBundles())
    }

    @Override
    void addListener(Consumer<WorkloadIds> listener) {
        listener.accept(get())
    }

}