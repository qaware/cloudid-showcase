package de.qaware.cloudid.lib.vault;

import lombok.RequiredArgsConstructor;

import java.util.Set;

/**
 * Represents an Access Control List (ACL) with entries of the pattern "client with a SPIFFE ID clientId is allowed to
 * access server with SPIFFE ID serverId".
 */
@RequiredArgsConstructor
public class ACL {

    private final Set<ACLEntry> entries;

    /**
     * Checks if a client is allowed to access a server
     * @param clientId the SPIFFE ID of the client wanting to access the server
     * @param serverId the SPIFFE ID of the server
     * @return true if the client is allowed to access the server
     */
    public boolean isAllowed(String clientId, String serverId) {
        return entries.contains(new ACLEntry(clientId, serverId));
    }

}
