package de.qaware.cloud.id.spire.impl;

import de.qaware.cloud.id.spire.SVIDBundle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedKeyManager;
import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.function.Supplier;

/**
 * X.509 key manager backed by a SPIFFE SVId.
 */
@Slf4j
@RequiredArgsConstructor
class SpiffeKeyManager extends X509ExtendedKeyManager {

    private final Supplier<SVIDBundle> bundleSupplier;

    // TODO: Take the algorithm into account

    @Override
    public String[] getClientAliases(String algorithm, Principal[] principals) {
        LOGGER.info("Get client aliases for {}, {}", algorithm, principals);
        return new String[]{this.bundleSupplier.get().getSvId()};
    }

    @Override
    public String chooseClientAlias(String[] algorithms, Principal[] principals, Socket socket) {
        LOGGER.info("Choose client alias for {}, {}, {}", algorithms, principals, socket);
        return this.bundleSupplier.get().getSvId();
    }

    @Override
    public String[] getServerAliases(String algorithm, Principal[] principals) {
        LOGGER.info("Get server aliases for {}, {}", algorithm, principals);
        return new String[]{this.bundleSupplier.get().getSvId()};
    }

    @Override
    public String chooseServerAlias(String algorithm, Principal[] principals, Socket socket) {
        LOGGER.info("Choose server alias for {}, {}, {}", algorithm, principals, socket);
        return this.bundleSupplier.get().getSvId();
    }

    @Override
    public X509Certificate[] getCertificateChain(String alias) {
        LOGGER.info("Get certificate chain for {}", alias);
        return new X509Certificate[]{bundleSupplier.get().getCertificate()};
    }

    @Override
    public PrivateKey getPrivateKey(String alias) {
        LOGGER.info("Get private key for {}", alias);
        return bundleSupplier.get().getKeyPair().getPrivate();
    }

    @Override
    public String chooseEngineClientAlias(String[] algorithms, Principal[] principals, SSLEngine sslEngine) {
        LOGGER.info("Choose client alias for {}, {}, {}", algorithms, principals, sslEngine);
        return this.bundleSupplier.get().getSvId();
    }

    @Override
    public String chooseEngineServerAlias(String algorithm, Principal[] principals, SSLEngine sslEngine) {
        LOGGER.info("Choose server alias for {}, {}, {}", algorithm, principals, sslEngine);
        return this.bundleSupplier.get().getSvId();
    }
}
