package de.qaware.cloudid.lib.jsa;

import de.qaware.cloudid.lib.CloudId;
import de.qaware.cloudid.lib.IdManager;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.security.cert.Certificate;
import java.time.Instant;
import java.util.Date;
import java.util.Enumeration;
import java.util.Objects;

import static java.util.Collections.enumeration;
import static java.util.Collections.singleton;

/**
 * CloudId key store.
 * <p>
 * Uses a fixed alias.
 */
@Slf4j
public class CloudIdKeyStore extends ROVirtualKeyStoreSPI {

    private final IdManager idManager;

    /**
     * Constructor
     */
    public CloudIdKeyStore() {
        idManager = CloudId.getIdManager();
    }

    @Override
    public Key engineGetKey(String alias, char[] password) {
        LOGGER.trace("engineGetKey({}, ...)", alias);
        return idManager.getSingleBundle().getKeyPair().getPrivate();
    }

    @Override
    public Certificate[] engineGetCertificateChain(String alias) {
        LOGGER.trace("engineGetCertificateChain({})", alias);

        return idManager.getSingleBundle().getCaCertChainArray();
    }

    @Override
    public Certificate engineGetCertificate(String alias) {
        LOGGER.trace("engineGetCertificate({})", alias);
        return idManager.getSingleBundle().getCertificate();
    }

    @Override
    public Date engineGetCreationDate(String alias) {
        LOGGER.trace("engineGetCreationDate({})", alias);
        return Date.from(Instant.EPOCH);
    }

    @Override
    public Enumeration<String> engineAliases() {
        LOGGER.trace("engineAliases()");
        return enumeration(singleton(CloudId.SINGLE_ALIAS));
    }

    @Override
    public boolean engineContainsAlias(String alias) {
        LOGGER.trace("engineContainsAlias({})", alias);
        return Objects.equals(alias, CloudId.SINGLE_ALIAS);
    }

    @Override
    public int engineSize() {
        LOGGER.trace("engineSize()");
        return 1;
    }

    @Override
    public boolean engineIsKeyEntry(String alias) {
        LOGGER.trace("engineIsKeyEntry({})", alias);
        return Objects.equals(alias, CloudId.SINGLE_ALIAS);
    }

    @Override
    public boolean engineIsCertificateEntry(String alias) {
        LOGGER.trace("engineIsCertificateEntry({})", alias);
        return Objects.equals(alias, CloudId.SINGLE_ALIAS);
    }

    @Override
    public String engineGetCertificateAlias(Certificate cert) {
        LOGGER.trace("engineGetCertificateAlias({})", cert);
        return CloudId.SINGLE_ALIAS;
    }

}
