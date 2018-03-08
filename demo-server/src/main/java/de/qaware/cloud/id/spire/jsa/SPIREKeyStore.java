package de.qaware.cloud.id.spire.jsa;

import de.qaware.cloud.id.spire.Bundle;
import de.qaware.cloud.id.spire.StaticLauncher;
import de.qaware.cloud.id.util.ReadOnlyKeyStore;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.security.Key;
import java.security.cert.Certificate;
import java.time.Instant;
import java.util.Date;
import java.util.Enumeration;
import java.util.Objects;
import java.util.function.Supplier;

import static de.qaware.cloud.id.spire.jsa.SPIREJSAUtils.getCertChain;
import static java.util.Collections.enumeration;
import static java.util.Collections.singleton;

/**
 * SPIRE key store.
 * <p>
 * Uses a fixed alias.
 */
@Slf4j
public class SPIREKeyStore extends ReadOnlyKeyStore {

    private final Supplier<Bundle> bundleSupplier;

    /**
     * Constructor
     */
    public SPIREKeyStore() {
        bundleSupplier = StaticLauncher.getBundleSupplier();
    }

    @Override
    public Key engineGetKey(String alias, char[] password) {
        LOGGER.trace("engineGetKey({}, {})", alias, password);
        return bundleSupplier.get().getKeyPair().getPrivate();
    }

    @Override
    public Certificate[] engineGetCertificateChain(String alias) {
        LOGGER.trace("engineGetCertificateChain({})", alias);

        return getCertChain(bundleSupplier.get());
    }

    @Override
    public Certificate engineGetCertificate(String alias) {
        LOGGER.trace("engineGetCertificate({})", alias);
        return bundleSupplier.get().getCertificate();
    }

    @Override
    public Date engineGetCreationDate(String alias) {
        LOGGER.trace("engineGetCreationDate({})", alias);
        return Date.from(Instant.EPOCH);
    }

    @Override
    public Enumeration<String> engineAliases() {
        LOGGER.trace("engineAliases()");
        return enumeration(singleton(SPIREProvider.ALIAS));
    }

    @Override
    public boolean engineContainsAlias(String alias) {
        LOGGER.trace("engineContainsAlias({})", alias);
        return Objects.equals(alias, SPIREProvider.ALIAS);
    }

    @Override
    public int engineSize() {
        LOGGER.trace("engineSize()");
        return 1;
    }

    @Override
    public boolean engineIsKeyEntry(String alias) {
        LOGGER.trace("engineIsKeyEntry({})", alias);
        return Objects.equals(alias, SPIREProvider.ALIAS);
    }

    @Override
    public boolean engineIsCertificateEntry(String alias) {
        LOGGER.trace("engineIsCertificateEntry({})", alias);
        return Objects.equals(alias, SPIREProvider.ALIAS);
    }

    @Override
    public String engineGetCertificateAlias(Certificate cert) {
        LOGGER.trace("engineGetCertificateAlias({})", cert);
        return SPIREProvider.ALIAS;
    }

    @Override
    public void engineLoad(InputStream stream, char[] password) {
        LOGGER.trace("engineLoad({}, ...)", stream);
    }

}
