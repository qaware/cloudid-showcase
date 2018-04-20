package de.qaware.cloudid.lib;

import de.qaware.cloudid.util.Updater;

import java.util.function.Consumer;

/**
 * Interface for supplying an ACL that can be used to verify if a client is allowed to access a server
 */
public interface ACLManager extends Updater<ACL> {

    /**
     * Get the current ACL.
     * <p>
     * Blocks until an ACL becomes available.
     *
     * @return ACL
     */
    ACL get();

    /**
     * Add a listener that gets notified whenever the ACL changes.
     * <p>
     * Listeners will be notified immediately if an ACL was available before they are added.
     *
     * @param listener listener
     */
    void addListener(Consumer<ACL> listener);

}
