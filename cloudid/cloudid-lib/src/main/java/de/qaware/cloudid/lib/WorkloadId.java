package de.qaware.cloudid.lib;

import lombok.Data;

import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;

/**
 * Represents a workload identity.
 * <p>
 * A workload identity consists of the following parts:
 * <ul>
 * <li>spiffeId: The business identity in SPIFFE format</li>
 * <li>svid, keyPair, chain: The technical identity as SPIFFE SVID</li>
 * <li>federatedCAs: A set of CAs that should also be trusted</li>
 * </ul>
 */
@Data
public class WorkloadId {

    private final String spiffeId;
    private final X509Certificate svid;
    private final KeyPair keyPair;
    private final List<X509Certificate> chain;
    private final Map<String, List<X509Certificate>> federatedCAs;

    /**
     * Get a copy of the svid chain as an array.
     *
     * @return svid chain as an array
     */
    public X509Certificate[] getCaCertChainArray() {
        return chain.toArray(new X509Certificate[0]);
    }

    /**
     * Get the trusted CAs.
     * <p>
     * The trusted CAs consist of the SPIRE CA and the federated SPIRE CAs.
     *
     * @return trusted CAs
     */
    public Set<X509Certificate> getTrustedCAs() {
        return concat(
                Stream.of(chain.get(chain.size() - 1)),
                federatedCAs.values().stream().map(l -> l.get(0)))
                .collect(toSet());
    }

}
