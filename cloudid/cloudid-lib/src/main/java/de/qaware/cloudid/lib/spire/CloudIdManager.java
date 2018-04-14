package de.qaware.cloudid.lib.spire;

import org.apache.commons.lang3.Validate;

import java.util.List;
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
     * Get the single bundle if there is only one.
     *
     * @return bundle
     */
    default Bundle getSingleBundle() {
        List<Bundle> bundleList = getBundles().getBundleList();
        Validate.isTrue(bundleList.size() == 1);
        return bundleList.get(0);
    }

    /**
     * Add a listener that gets notified whenever the current set of bundles changes.
     *
     * Listeners will be notified immediately if bundles were available before they are added.
     *
     * @param listener listener
     */
    void addListener(Consumer<Bundles> listener);

}
