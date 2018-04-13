package de.qaware.cloudid.lib.jsa;

import com.bettercloud.vault.SslConfig;
import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import com.bettercloud.vault.response.AuthResponse;
import de.qaware.cloudid.lib.spire.Bundle;
import de.qaware.cloudid.lib.spire.CloudIdManager;
import de.qaware.cloudid.lib.spire.Config;
import de.qaware.cloudid.lib.util.ACLParser;
import de.qaware.cloudid.lib.util.Certificates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static de.qaware.cloudid.lib.util.Certificates.getSpiffeId;
import static java.util.Arrays.stream;

/**
 * X.509 trust manager backed by SPIRE bundles.
 */
@Slf4j
@RequiredArgsConstructor
public class SPIRETrustManager extends X509ExtendedTrustManager {

    private static final String VAULT_ADDR = getVaultAddress();
    private static final String DEFAULT_LOCALHOST_VAULT_ADDR = "https://localhost:8200";
    private final CloudIdManager cloudIdManager;

    private final X509TrustManager delegate = getDefaultTrustManager();

    private static String getVaultAddress() {
        String address = System.getProperty("cloudid.vault.address");
        if (address == null) {
            address = DEFAULT_LOCALHOST_VAULT_ADDR;
        }
        LOGGER.debug("Vault address: {}", address);
        return address;
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        LOGGER.trace("getAcceptedIssuers()");

        return cloudIdManager.getPreferredBundle().getTrustedCAs().toArray(new X509Certificate[0]);
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        LOGGER.debug("Validating client {}, {}", chain, authType);

        Validate.notEmpty(chain);

        Optional<String> clientIdOpt = getSpiffeId(chain[0]);

        if (clientIdOpt.isPresent()) {
            String clientId = clientIdOpt.get();

            Bundle svid = cloudIdManager.getPreferredBundle();

            if (LOGGER.isDebugEnabled() && clientId.matches(".*/curl-client")) {
                LOGGER.info("Not verifying curl client cert");
                return;
            }

            if (shouldCheckAcl() && !ACLParser.isClientAllowed(getAcl(), clientId, svid.getSpiffeId())) {
                throw new CertificateException("Client could not be verified against the provided ACL");
            }

            Certificates.validate(chain, svid.getTrustedCAs());
        } else {
            LOGGER.debug("Client certificate is not a SPIFFE certificate. Delegating to {}", delegate.getClass().getName());
            delegate.checkClientTrusted(chain, authType);
        }
    }

    private String getAcl() throws CertificateException {
        // TODO: Refactor
        Map<String, String> vaultData = queryVault("secret/acl");

        LOGGER.debug("Vault Response Data: {}", vaultData);

        if (vaultData == null || vaultData.isEmpty() || !vaultData.containsKey("acl")) {
            throw new CertificateException("Vault response was empty or did not contain the key 'acl'. Rejecting client certificate before checking the chain");
        }

        return vaultData.get("acl");

    }

    private Map<String, String> queryVault(String path) {
        // TODO: Re-use the vault connection
        try {
            KeyStore keyStore = KeyStore.getInstance("SPIRE");
            keyStore.load(null, "".toCharArray());

            VaultConfig config = new VaultConfig()
                    .address(VAULT_ADDR)
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

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {
        checkClientTrusted(chain, authType);
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine sslEngine) throws CertificateException {
        checkClientTrusted(chain, authType);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        LOGGER.debug("Validating server {}, {}", chain, authType);

        Validate.notEmpty(chain);

        Optional<String> clientIdOpt = getSpiffeId(chain[0]);
        if (clientIdOpt.isPresent()) {
            Certificates.validate(chain, cloudIdManager.getPreferredBundle().getTrustedCAs());
        } else {
            LOGGER.debug("Server certificate is not a SPIFFE certificate. Delegating to {}", delegate.getClass().getName());
            delegate.checkServerTrusted(chain, authType);
        }
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {
        checkServerTrusted(chain, authType);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine sslEngine) throws CertificateException {
        checkServerTrusted(chain, authType);
    }

    private boolean shouldCheckAcl() {
        return !Config.ACL_DISABLED.get();
    }

    private static X509TrustManager getDefaultTrustManager() {
        return stream(Security.getProviders("TrustManagerFactory.PKIX"))
                .map(Provider::getName)
                .filter(name -> !Objects.equals(name, SPIREProvider.NAME))
                .map(SPIRETrustManager::getX509TrustManager)
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    private static X509TrustManager getX509TrustManager(String name) {
        try {
            TrustManagerFactory factory = TrustManagerFactory.getInstance("PKIX", name);
            factory.init((KeyStore) null);
            return stream(factory.getTrustManagers())
                    .filter(tm -> tm instanceof X509TrustManager)
                    .map(tm -> (X509TrustManager) tm)
                    .findFirst()
                    .orElseThrow(IllegalStateException::new);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | KeyStoreException e) {
            throw new IllegalStateException(e);
        }
    }

}
