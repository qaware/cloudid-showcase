package de.qaware.cloudid.lib.jsa;

import de.qaware.cloudid.lib.IdManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedKeyManager;
import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Objects;

import static de.qaware.cloudid.lib.CloudId.SINGLE_ALIAS;

/**
 * X.509 key manager backed by cloud identities.
 */
@Slf4j
@RequiredArgsConstructor
public class CloudIdKeyManager extends X509ExtendedKeyManager {

    private final IdManager idManager;

    @Override
    public PrivateKey getPrivateKey(String alias) {
        LOGGER.trace("getPrivateKey({})", alias);

        if (!Objects.equals(alias, SINGLE_ALIAS)) {
            return null;
        }

        return idManager.getWorkloadId().getKeyPair().getPrivate();
    }

    @SuppressWarnings("squid:S1168" /* null is required by the interface to signal that the chain is not available */)
    @Override
    public X509Certificate[] getCertificateChain(String alias) {
        LOGGER.trace("getCertificateChain({})", alias);

        if (!Objects.equals(alias, SINGLE_ALIAS)) {
            return null;
        }

        return idManager.getWorkloadId().getCaCertChainArray();
    }

    @Override
    public String[] getClientAliases(String keyType, Principal[] issuers) {
        LOGGER.trace("Get client aliases for {}, {}", keyType, issuers);
        return getAliases(keyType);
    }

    @Override
    public String chooseClientAlias(String[] keyTypes, Principal[] issuers, Socket socket) {
        LOGGER.trace("Choose client alias for {}, {}, {}", keyTypes, issuers, socket);
        return getAlias(keyTypes);
    }

    @Override
    public String chooseEngineClientAlias(String[] keyTypes, Principal[] issuers, SSLEngine sslEngine) {
        LOGGER.trace("Choose client alias for {}, {}, {}", keyTypes, issuers, sslEngine);
        return getAlias(keyTypes);
    }

    @Override
    public String[] getServerAliases(String keyType, Principal[] issuers) {
        LOGGER.trace("getServerAliases({}, {})", keyType, issuers);

        return getAliases(keyType);
    }

    @Override
    public String chooseEngineServerAlias(String keyType, Principal[] issuers, SSLEngine sslEngine) {
        LOGGER.trace("chooseEngineServerAlias({}, {}, {})", keyType, issuers, sslEngine);
        return getAlias(keyType);
    }

    @Override
    public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
        LOGGER.trace("chooseServerAlias({}, {}, {})", keyType, issuers, socket);
        return getAlias(keyType);
    }

    private String getAlias(String ...keyTypes) {
        String idAlgorithm = idManager.getWorkloadId().getKeyPair().getPrivate().getAlgorithm();

        for (String keyType : keyTypes) {
            if (keyType.equals(idAlgorithm)) {
                return SINGLE_ALIAS;
            }
        }

        return null;
    }

    private String[] getAliases(String keyType) {
        String alias = getAlias(keyType);
        if (alias != null) {
            return new String[]{alias};
        } else {
            return new String[0];
        }
    }


}
