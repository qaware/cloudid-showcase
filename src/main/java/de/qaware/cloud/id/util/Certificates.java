package de.qaware.cloud.id.util;

import lombok.NoArgsConstructor;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.*;
import java.time.Instant;
import java.util.Date;

import static java.util.Collections.singleton;
import static lombok.AccessLevel.PRIVATE;

/**
 * Utilities for certificates.
 */
@SuppressWarnings("squid:S1118") // Sonar rule is not Lombok aware
@NoArgsConstructor(access = PRIVATE)
public final class Certificates {


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

    /**
     * Get a X.509 certificate factory
     *
     * @return {@code CertificateFactory.getInstance("X.509")}
     */
    public static CertificateFactory getX509CertFactory() {
        try {
            return CertificateFactory.getInstance("X.509");
        } catch (CertificateException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Get a PKIX certificate path validator.
     *
     * @return {@code CertPathValidator.getInstance("PKIX")}
     */
    public static CertPathValidator getCertPathValidator() {
        try {
            return CertPathValidator.getInstance("PKIX");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Get PKIX parameters.
     * <p>
     * Certificate revocation will be disabled.
     *
     * @param certificate trust anchor
     * @return PKIX parameters
     */
    public static PKIXParameters getPkixParameters(X509Certificate certificate) {
        try {
            PKIXParameters pkixParameters = new PKIXParameters(singleton(new TrustAnchor(certificate, null)));
            pkixParameters.setRevocationEnabled(false);
            return pkixParameters;
        } catch (InvalidAlgorithmParameterException e) {
            throw new IllegalStateException(e);
        }
    }

}
