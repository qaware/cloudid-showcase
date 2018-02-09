package de.qaware.cloud.id.util;

import lombok.NoArgsConstructor;

import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.Date;

import static lombok.AccessLevel.PRIVATE;

/**
 * Utilities for certificates.
 */
@NoArgsConstructor(access = PRIVATE)
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
     * Get the notBefore instant of a X.509 certificate.
     *
     * @param certificate certificate
     * @return notBefore instant or {@code Instant#MIN} if the field is unset
     */
    public static Instant getNotBefore(X509Certificate certificate) {
        Date date = certificate.getNotBefore();
        return date != null ? date.toInstant() : Instant.MIN;
    }

}
