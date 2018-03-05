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
import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Collections.singleton;

/**
 * X.509 key manager backed by SPIRE bundles.
 */
@Slf4j
@RequiredArgsConstructor
public class SPIREKeyManager extends X509ExtendedKeyManager {

    private final Supplier<Bundle> bundleSupplier;

    @Override
    public PrivateKey getPrivateKey(String alias) {
        Bundle bundle = bundleSupplier.get();

        PrivateKey privateKey = Objects.equals(alias, bundle.getSvId())
                ? bundle.getKeyPair().getPrivate()
                : null;

        LOGGER.debug("getPrivateKey({}) = {}", alias, privateKey);
        return privateKey;
    }

    @Override
    public X509Certificate[] getCertificateChain(String alias) {
        Bundle bundle = bundleSupplier.get();

        X509Certificate[] certChain = Objects.equals(alias, bundle.getSvId())
                ? Stream.of(singleton(bundle.getCertificate()), bundle.getCaCertChain())
                .flatMap(Collection::stream)
                .toArray(X509Certificate[]::new)
                : null;

        LOGGER.debug("getCertificateChain({}) = {}", alias, certChain);
        return certChain;
    }

    @Override
    public String[] getClientAliases(String keyType, Principal[] issuers) {
        LOGGER.info("Get client aliases for {}, {}", keyType, issuers);
        return new String[]{this.bundleSupplier.get().getSvId()};
    }

    @Override
    public String chooseClientAlias(String[] keyTypes, Principal[] issuers, Socket socket) {
        LOGGER.info("Choose client alias for {}, {}, {}", keyTypes, issuers, socket);
        return this.bundleSupplier.get().getSvId();
    }

    @Override
    public String chooseEngineClientAlias(String[] keyTypes, Principal[] issuers, SSLEngine sslEngine) {
        LOGGER.info("Choose client alias for {}, {}, {}", keyTypes, issuers, sslEngine);
        return this.bundleSupplier.get().getSvId();
    }

    @Override
    public String[] getServerAliases(String keyType, Principal[] issuers) {
        return new String[]{chooseServerAlias(keyType, issuers)};
    }

    @Override
    public String chooseEngineServerAlias(String keyType, Principal[] issuers, SSLEngine sslEngine) {
        return chooseServerAlias(keyType, issuers);
    }

    @Override
    public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
        return chooseServerAlias(keyType, issuers);
    }

    private String chooseServerAlias(String keyType, Principal[] issuers) {
        Bundle bundle = bundleSupplier.get();

        String serverAlias = Objects.equals(keyType, bundle.getKeyType())
                ? bundle.getSvId()
                : null;

        LOGGER.debug("chooseServerAlias({}, {}) = {}", keyType, issuers, serverAlias);
        return serverAlias;
    }
}
