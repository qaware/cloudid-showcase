package de.qaware.cloud.id.util;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.Date;

/**
 * Utilities for certificates.
 */
public class Certificates {

    /**
     * Get the notAfter instant of a X.509 certificate.
     *
     * @param certificate certificate
     * @return notAfter instant or {@code Instant#MAX} if the field is unset
     */
    public static Instant getNotAfter(X509Certificate certificate) {
        Date date = certificate.getNotAfter();
        return date != null ? date.toInstant() : Instant.MAX;
    }

    /**
     * Get the notAfter instant of a certificate.
     *
     * @param certificate certificate
     * @return notAfter instant or {@code Instant#MAX} if the field is unset or the certificate is not a X.509
     * certificate
     */
    public static Instant getNotAfter(Certificate certificate) {
        return certificate instanceof X509Certificate ? getNotAfter((X509Certificate) certificate) : Instant.MAX;
    }

    /**
     * Get the notBefore instant of a X.509 certificate.
     *
     * @param certificate certificate
     * @return notBefore instant or {@code Instant#MIN} if the field is unset
     */
    public static Instant getNotBefore(X509Certificate certificate) {
        Date date = certificate.getNotBefore();
        return date != null ? date.toInstant() : Instant.MIN;
    }

    /**
     * Get the notBefore instant of a certificate.
     *
     * @param certificate certificate
     * @return notBefore instant or {@code Instant#MIN} if the field is unset or the certificate is not a X.509
     * certificate
     */
    public static Instant getNotBefore(Certificate certificate) {
        return certificate instanceof X509Certificate ? getNotBefore((X509Certificate) certificate) : Instant.MIN;
    }

    /**
     * No instantiation.
     */
    private Certificates() {
    }

}
