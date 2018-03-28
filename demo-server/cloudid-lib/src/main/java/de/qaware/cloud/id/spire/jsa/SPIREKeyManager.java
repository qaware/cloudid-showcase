package de.qaware.cloud.id.spire.jsa;

import de.qaware.cloud.id.spire.Bundle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedKeyManager;
import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Objects;
import java.util.function.Supplier;

import static de.qaware.cloud.id.spire.jsa.SPIREProvider.ALIAS;

/**
 * X.509 key manager backed by SPIRE bundles.
 */
@Slf4j
@RequiredArgsConstructor
public class SPIREKeyManager extends X509ExtendedKeyManager {

    private final Supplier<Bundle> bundleSupplier;

    @Override
    public PrivateKey getPrivateKey(String alias) {
        LOGGER.trace("getPrivateKey({})", alias);

        if (!Objects.equals(alias, ALIAS)) {
            return null;
        }

        return bundleSupplier.get().getKeyPair().getPrivate();
    }

    @SuppressWarnings("squid:S1168" /* null is required by the interface to signal that the chain is not available */)
    @Override
    public X509Certificate[] getCertificateChain(String alias) {
        LOGGER.trace("getCertificateChain({})", alias);

        if (!Objects.equals(alias, ALIAS)) {
            return null;
        }

        return bundleSupplier.get().getCaCertChainArray();
    }

    @Override
    public String[] getClientAliases(String keyType, Principal[] issuers) {
        LOGGER.trace("Get client aliases for {}, {}", keyType, issuers);
        return new String[]{ALIAS};
    }

    @Override
    public String chooseClientAlias(String[] keyTypes, Principal[] issuers, Socket socket) {
        LOGGER.trace("Choose client alias for {}, {}, {}", keyTypes, issuers, socket);
        return ALIAS;
    }

    @Override
    public String chooseEngineClientAlias(String[] keyTypes, Principal[] issuers, SSLEngine sslEngine) {
        LOGGER.trace("Choose client alias for {}, {}, {}", keyTypes, issuers, sslEngine);
        return ALIAS;
    }

    @Override
    public String[] getServerAliases(String keyType, Principal[] issuers) {
        LOGGER.trace("getServerAliases({}, {})", keyType, issuers);
        return new String[]{ALIAS};
    }

    @Override
    public String chooseEngineServerAlias(String keyType, Principal[] issuers, SSLEngine sslEngine) {
        LOGGER.trace("chooseEngineServerAlias({}, {}, {})", keyType, issuers, sslEngine);
        return ALIAS;
    }

    @Override
    public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
        LOGGER.trace("chooseServerAlias({}, {}, {})", keyType, issuers, socket);
        return ALIAS;
    }

}
