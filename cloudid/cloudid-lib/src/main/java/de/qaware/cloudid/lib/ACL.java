package de.qaware.cloudid.lib;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.Set;

/**
 * Represents an Access Control List (ACL) with entries of the pattern "client with a SPIFFE ID clientId is allowed to
 * access server with SPIFFE ID serverId".
 */
@EqualsAndHashCode
@RequiredArgsConstructor
public class ACL {

    private final Set<Entry> entries;

    /**
     * Checks if a client is allowed to access a server
     *
     * @param clientId the SPIFFE ID of the client wanting to access the server
     * @param serverId the SPIFFE ID of the server
     * @return true if the client is allowed to access the server
     */
    public boolean isAllowed(String clientId, String serverId) {
        return entries.contains(new Entry(clientId, serverId));
    }

    /**
     * Represents an entry of ACL which describes access allowance between client and server using SPIFFE IDs
     */
    @Data
    public static class Entry {
        private final String clientId;
        private final String serverId;
    }

}
