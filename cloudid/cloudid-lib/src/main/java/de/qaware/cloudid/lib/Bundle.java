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
 * <li>certificate, keyPair, caCertChain: The technical identity as SPIFFE SVID</li>
 * <li>federatedBundles: A set of CAs that should also be trusted</li>
 * </ul>
 */
@Data
public class Bundle {

    private final String spiffeId;
    private final X509Certificate certificate;
    private final KeyPair keyPair;
    private final List<X509Certificate> caCertChain;
    private final Map<String, List<X509Certificate>> federatedBundles;

    /**
     * Get a copy of the certificate chain as an array.
     *
     * @return certificate chain as an array
     */
    public X509Certificate[] getCaCertChainArray() {
        return caCertChain.toArray(new X509Certificate[0]);
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
                Stream.of(caCertChain.get(caCertChain.size() - 1)),
                federatedBundles.values().stream().map(l -> l.get(0)))
                .collect(toSet());
    }

}
