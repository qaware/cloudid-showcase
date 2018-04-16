package de.qaware.cloudid.lib.vault;

import lombok.Data;

/**
 * Representing an entry of ACL which describes access allowance between client and server using SPIFFE IDs
 */
@Data
public class ACLEntry {
    private final String clientId;
    private final String serverId;
}
