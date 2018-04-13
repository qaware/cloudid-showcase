package de.qaware.cloudid.lib.spire;

import java.util.function.Consumer;

/**
 * Manages SPIRE bundles.
 */
public interface CloudIdManager {

    /**
     * Start updating bundles.
     */
    void start();

    /**
     * Stop updating bundles.
     */
    void stop();

    /**
     * Get the current set of bundles.
     * <p>
     * Blocks until bundles become available.
     *
     * @return bundles
     */
    Bundles getBundles();

    /**
     * Get the bundle that contains the workload Id.
     *
     * @return bundle
     */
    default Bundle getPreferredBundle() {
        return getBundles().getBundleList().get(0);
    }

    /**
     * Add a listener that gets notified whenever the current set of bundles changes.
     *
     * @param listener listener
     */
    void addListener(Consumer<Bundles> listener);

}
