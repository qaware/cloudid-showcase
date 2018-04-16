package de.qaware.cloudid.lib.jsa;

import de.qaware.cloudid.lib.CloudId;
import de.qaware.cloudid.lib.IdManager;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.*;

import static java.util.Collections.enumeration;
import static java.util.stream.Collectors.toList;

/**
 * Trust store backed by cloud Ids.
 */
@Slf4j
public class CloudIdTrustStore extends ROVirtualKeyStoreSPI {

    private final IdManager idManager;

    /**
     * Constructor
     */
    public CloudIdTrustStore() {
        idManager = CloudId.getIdManager();
    }

    @Override
    public Key engineGetKey(String alias, char[] password) {
        LOGGER.trace("engineGetKey({}, ...)", alias);
        return null;
    }


    @Override
    public Certificate[] engineGetCertificateChain(String alias) {
        LOGGER.trace("engineGetCertificateChain({})", alias);
        return getCertificate(alias).map(c -> new Certificate[]{c}).orElse(null);
    }

    @Override
    public Certificate engineGetCertificate(String alias) {
        LOGGER.trace("engineGetCertificate({})", alias);
        return getCertificate(alias).orElse(null);
    }

    @Override
    public Date engineGetCreationDate(String alias) {
        LOGGER.trace("engineGetCreationDate({})", alias);
        return Date.from(Instant.EPOCH);
    }

    @Override
    public Enumeration<String> engineAliases() {
        LOGGER.trace("engineAliases()");
        return enumeration(
                getTrustedCAs().stream()
                        .map(this::getAlias)
                        .collect(toList()));
    }

    @Override
    public boolean engineContainsAlias(String alias) {
        LOGGER.trace("engineContainsAlias({})", alias);
        return getCertificate(alias).isPresent();
    }

    @Override
    public int engineSize() {
        LOGGER.trace("engineSize()");
        return getTrustedCAs().size();
    }

    @Override
    public boolean engineIsKeyEntry(String alias) {
        LOGGER.trace("engineIsKeyEntry({})", alias);
        return false;
    }

    @Override
    public boolean engineIsCertificateEntry(String alias) {
        LOGGER.trace("engineIsCertificateEntry({})", alias);
        return getCertificate(alias).isPresent();
    }

    @Override
    public String engineGetCertificateAlias(Certificate cert) {
        LOGGER.trace("engineGetCertificateAlias({})", cert);
        return getAlias(cert);
    }

    private String getAlias(Certificate certificate) {
        try {
            return Base64.getEncoder().encodeToString(
                    MessageDigest.getInstance("SHA-256").digest(
                            certificate.getEncoded()));
        } catch (NoSuchAlgorithmException | CertificateEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Optional<X509Certificate> getCertificate(String alias) {
        return getTrustedCAs().stream()
                .filter(c -> Objects.equals(getAlias(c), alias))
                .findFirst();
    }

    private Set<X509Certificate> getTrustedCAs() {
        return idManager.getSingleBundle().getTrustedCAs();
    }

}
