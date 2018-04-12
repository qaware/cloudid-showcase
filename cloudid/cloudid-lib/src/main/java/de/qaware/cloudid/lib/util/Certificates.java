package de.qaware.cloudid.lib.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.security.auth.x500.X500Principal;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.*;
import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;

/**
 * Utilities for certificates.
 */
@Slf4j
@UtilityClass
public class Certificates {

    private static final Pattern SPIFFE_ID_PATTERN = Pattern.compile("spiffe://.+");

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

    private static PKIXParameters toPkixParameters(Set<X509Certificate> trustedCerts) {
        try {
            PKIXParameters pkixParameters = new PKIXParameters(trustedCerts.stream()
                    .map(c -> new TrustAnchor(c, null))
                    .collect(toSet()));
            pkixParameters.setRevocationEnabled(false);
            return pkixParameters;
        } catch (InvalidAlgorithmParameterException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Validate a certificate chain against a set of trusted certificates.
     *
     * @param chain        certificate chain
     * @param trustedCerts trusted certificates
     * @throws CertificateException if the validation fails
     */
    public void validate(X509Certificate[] chain, Set<X509Certificate> trustedCerts) throws CertificateException {
        PKIXParameters pkixParameters = toPkixParameters(trustedCerts);
        CertPath certPath = getX509CertFactory().generateCertPath(truncateChain(chain, trustedCerts));

        try {
            getCertPathValidator().validate(certPath, pkixParameters);
        } catch (CertPathValidatorException | InvalidAlgorithmParameterException e) {
            throw new CertificateException(e);
        }
    }

    /**
     * Get the SPIFFE Id from a SPIFFE certificate.
     *
     * @param certificate certificate
     * @return optional containing the SPIFFE Id
     */
    public static Optional<String> getSpiffeId(X509Certificate certificate) throws CertificateParsingException {
        Collection<List<?>> sans = certificate.getSubjectAlternativeNames();

        if (sans == null) {
            return Optional.empty();
        }

        return sans.stream()
                .flatMap(List::stream)
                .filter(o -> o instanceof String)
                .map(o -> (String) o)
                .filter(s -> SPIFFE_ID_PATTERN.matcher(s).matches())
                .findFirst();
    }

    private static List<X509Certificate> truncateChain(X509Certificate[] chain, Set<X509Certificate> trustedCerts) throws CertificateException {
        Set<X500Principal> trustedPrincipals = trustedCerts.stream()
                .map(X509Certificate::getSubjectX500Principal)
                .collect(toSet());

        for (int i = 0; i < chain.length; i++) {
            X509Certificate certificate = chain[i];
            if (trustedPrincipals.contains(certificate.getIssuerX500Principal())) {
                return asList(chain).subList(0, i);
            }
        }

        throw new CertificateException("Path does not chain with any of the trust anchors");
    }

}
