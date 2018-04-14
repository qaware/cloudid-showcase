package de.qaware.cloudid.lib.spire;

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
 * A data transfer object that contains the parsed information about a spiffe workload id.
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
     * The  trusted CAs consist of the SPIRE CA and the federated SPIRE CAs.
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
