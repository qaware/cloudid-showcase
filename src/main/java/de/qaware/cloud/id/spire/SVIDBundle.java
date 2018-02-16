package de.qaware.cloud.id.spire;

import de.qaware.cloud.id.util.Certificates;
import lombok.Data;

import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.List;

/**
 * A data transfer object that contains the parsed informations about a spiffe workload id.
 */
@Data
public class SVIDBundle {

    private final String svId;
    private final X509Certificate certificate;
    private final KeyPair keyPair;
    private final List<X509Certificate> caCertChain;

    /**
     * Get the key type.
     *
     * @return key type / algorithm
     */
    public String getKeyType() {
        return keyPair.getPublic().getAlgorithm();
    }

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

}
