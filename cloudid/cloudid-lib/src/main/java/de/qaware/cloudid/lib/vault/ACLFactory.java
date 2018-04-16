package de.qaware.cloudid.lib.vault;

/**
 * Interface for supplying an ACL that can be used to verify if a client is allowed to access a server
 */
public interface ACLFactory {
    /**
     * Returns a current ACL
     * @return current ACL
     */
    ACL get();
}
