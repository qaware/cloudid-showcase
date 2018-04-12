package de.qaware.cloudid.lib.spire;

import de.qaware.cloudid.lib.util.Certificates;
import lombok.Data;

import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

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
     * Get the first instant after which this bundle will no longer be valid.
     *
     * @return first instant after which this bundle will no longer be valid
     */
    public Instant getNotAfter() {
        Instant result = Certificates.getNotAfter(certificate);

        for (X509Certificate caCert : caCertChain) {
            Instant notAfter = Certificates.getNotAfter(caCert);
            if (notAfter.isBefore(result)) {
                result = notAfter;
            }
        }

        return result;
    }

    /**
     * Get the instant after which the certificate chain of this bundle will be valid.
     *
     * @return instant after which the certificate chain of this bundle will be valid.
     */
    public Instant getNotBefore() {
        Instant result = Certificates.getNotBefore(certificate);

        for (X509Certificate caCert : caCertChain) {
            Instant notBefore = Certificates.getNotBefore(caCert);
            if (notBefore.isAfter(result)) {
                result = notBefore;
            }
        }

        return result;
    }

    /**
     * Get a copy of the certificate chain as an array.
     *
     * @return certificate chain as an array
     */
    public X509Certificate[] getCaCertChainArray() {
        return caCertChain.toArray(new X509Certificate[0]);
    }

    /**
     * Get the SPIRE CA.
     *
     * @return SPIRE CA
     */
    public X509Certificate getSpireCa() {
        return caCertChain.get(caCertChain.size() - 1);
    }

    /**
     * Get the trusted CAs.
     * <p>
     * The  trusted CAs consist of the SPIRE CA and the federated SPIRE CAs.
     *
     * @return trusted CAs
     */
    public Set<X509Certificate> getTrustedCAs() {
        Set<X509Certificate> result = new HashSet<>(federatedBundles.size() + 1);
        result.add(getSpireCa());
        result.addAll(federatedBundles.values().stream()
                .map(l -> l.get(0))
                .collect(toSet()));

        return result;
    }

}
