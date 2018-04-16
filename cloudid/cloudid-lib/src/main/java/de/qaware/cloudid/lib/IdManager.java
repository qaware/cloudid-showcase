package de.qaware.cloudid.lib;

import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.function.Consumer;

/**
 * Manages Id bundles.
 */
public interface IdManager extends Updater<Bundles> {

    /**
     * Get the current set of bundles.
     * <p>
     * Blocks until bundles become available.
     *
     * @return bundles
     */
    Bundles get();

    /**
     * Add a listener that gets notified whenever the current set of bundles changes.
     *
     * Listeners will be notified immediately if bundles were available before they are added.
     *
     * @param listener listener
     */
    void addListener(Consumer<Bundles> listener);

    /**
     * Get the single bundle if there is only one.
     *
     * @return bundle
     */
    default Bundle getSingleBundle() {
        List<Bundle> bundleList = get().getBundleList();
        Validate.isTrue(bundleList.size() == 1);
        return bundleList.get(0);
    }

}
