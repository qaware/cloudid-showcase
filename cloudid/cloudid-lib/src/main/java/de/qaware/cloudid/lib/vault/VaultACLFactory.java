package de.qaware.cloudid.lib.vault;

import com.bettercloud.vault.SslConfig;
import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import com.bettercloud.vault.response.AuthResponse;
import de.qaware.cloudid.lib.spire.Config;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.*;

/**
 * Retrieves the ACL from Vault using VAULT_ADDRESS property {@link Config} to connect to a running Vault server
 */
@Slf4j
public class VaultACLFactory implements ACLFactory {

    /**
     * Queries Vault and converts the ACL into a {@link ACL} instance
     * @return the retrieved ACL
     */
    @Override
    public ACL get() {
        return new ACL(stream(split(getAcl(), "\n"))
                .map(s -> splitByWholeSeparator(s, "->"))
                .map(a -> new ACLEntry(trim(a[0]), trim(a[1])))
                .collect(toSet()));
    }

    private String getAcl() {
        Map<String, String> vaultData = queryVault(Config.VAULT_ACL_SECRET_PATH.get());

        LOGGER.debug("Vault Response Data: {}", vaultData);

        if (vaultData == null || vaultData.isEmpty() || !vaultData.containsKey("acl")) {
            throw new ACLException("Vault response was empty or did not contain the key 'acl'");
        }

        return vaultData.get("acl");
    }

    private Map<String, String> queryVault(String path) {
        // TODO: Re-use the vault connection
        try {
            KeyStore keyStore = KeyStore.getInstance("SPIRE");
            keyStore.load(null, "".toCharArray());

            VaultConfig config = new VaultConfig()
                    .address(Config.VAULT_ADDRESS.get())
                    .sslConfig(new SslConfig()
                            .keyStore(keyStore, "")
                            .trustStore(keyStore)
                            .build())
                    .build();
            Vault vault = new Vault(config);

            AuthResponse response = vault.auth().loginByCert();
            config.token(response.getAuthClientToken());

            return vault.logical().read(path).getData();
        } catch (VaultException | GeneralSecurityException | IOException e) {
            LOGGER.error("Error querying vault", e);
            throw new IllegalStateException(e);
        }
    }
}
