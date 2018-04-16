package de.qaware.cloudid.lib.vault;

import com.bettercloud.vault.SslConfig;
import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import com.bettercloud.vault.response.AuthResponse;
import de.qaware.cloudid.lib.ACL;
import de.qaware.cloudid.lib.ACLException;
import de.qaware.cloudid.lib.CloudId;
import de.qaware.cloudid.lib.Config;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.*;

/**
 * Provides ACLs from Vault.
 */
@Slf4j
class VaultACLSupplier implements Supplier<ACL> {
    private static final char RECORD_SEPARATOR = '\n';
    private static final String CLIENT_SERVER_SEPARATOR = "->";

    private static final String VAULT_ACL_KEY = "acl";

    private Vault vault;
    private volatile boolean shouldReconnect;

    @Override
    public ACL get() {
        return new ACL(stream(split(getAclString(), RECORD_SEPARATOR))
                .map(s -> splitByWholeSeparator(s, CLIENT_SERVER_SEPARATOR))
                .map(a -> new ACL.Entry(trim(a[0]), trim(a[1])))
                .collect(toSet()));
    }

    private String getAclString() {
        String vaultPath = Config.VAULT_ACL_SECRET_PATH.get();
        LOGGER.trace("Querying ACL from {}", vaultPath);

        Map<String, String> vaultData = queryVault(vaultPath);

        LOGGER.trace("Vault Response Data: {}", vaultData);

        if (vaultData == null || vaultData.isEmpty() || !vaultData.containsKey(VAULT_ACL_KEY)) {
            throw new ACLException("Vault response was empty or did not contain the key 'acl'");
        }

        return vaultData.get(VAULT_ACL_KEY);
    }

    private Map<String, String> queryVault(String path) {
        try {
            return getVault().logical().read(path).getData();
        } catch (VaultException e) {
            LOGGER.error("Error querying vault", e);
            throw new ACLException(e);
        }
    }

    private synchronized Vault getVault() {
        if (shouldReconnect || vault == null) {
            vault = connect();
            shouldReconnect = false;
        }

        return vault;
    }

    private Vault connect() {
        try {
            VaultConfig config = new VaultConfig()
                    .address(Config.VAULT_ADDRESS.get())
                    .sslConfig(new SslConfig()
                            .keyStore(getKeyStore(), "")
                            .trustStore(getTrustStore())
                            .build())
                    .build();
            Vault vaultConnection = new Vault(config);

            AuthResponse response = vaultConnection.auth().loginByCert();
            config.token(response.getAuthClientToken());
            return vaultConnection;
        } catch (VaultException e) {
            shouldReconnect = true;
            throw new ACLException(e);
        }
    }

    private static KeyStore getKeyStore() {
        return loadKeyStore(CloudId.ALGORITHM);
    }

    private static KeyStore getTrustStore() {
        return loadKeyStore(CloudId.TRUST_STORE_ALGORITHM);
    }

    private static KeyStore loadKeyStore(String algorithm) {
        try {
            KeyStore keyStore = KeyStore.getInstance(algorithm);
            keyStore.load(null, "".toCharArray());
            return keyStore;
        } catch (GeneralSecurityException | IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
