package de.qaware.cloudid.lib.jsa;

import com.bettercloud.vault.SslConfig;
import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import de.qaware.cloudid.lib.spire.Bundle;
import de.qaware.cloudid.lib.util.ACLParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static de.qaware.cloudid.lib.util.Certificates.*;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;

/**
 * X.509 trust manager backed by SPIRE bundles.
 */
@Slf4j
@RequiredArgsConstructor
public class SPIRETrustManager extends X509ExtendedTrustManager {

    private static final String VAULT_ADDR = "https://localhost:8200";
    private final Supplier<Bundle> bundleSupplier;

    private final X509TrustManager delegate = getDefaultTrustManager();

    private static X509TrustManager getDefaultTrustManager() {
        TrustManagerFactory factory = SPIREProvider.DEFAULT_TRUST_MANAGER_FACTORY;

        try {
            factory.init((KeyStore) null);
        } catch (KeyStoreException e) {
            throw new IllegalStateException(e);
        }

        return stream(factory.getTrustManagers())
                .filter(tm -> tm instanceof X509TrustManager)
                .map(tm -> (X509TrustManager) tm)
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        LOGGER.trace("getAcceptedIssuers()");
        return new X509Certificate[]{getRootCA()};
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        LOGGER.debug("Validating client {}, {}", chain, authType);

        Validate.notEmpty(chain);

        Collection<List<?>> sans = chain[0].getSubjectAlternativeNames();
        String clientId = getSpiffeId(sans);
        if (clientId != null) {
            // our SPIFFE ID
            String serverId = bundleSupplier.get().getSpiffeId();

            if (!ACLParser.isClientAllowed(getAcl(), clientId, serverId)) {
                throw new CertificateException("Client could not be verified against the provided ACL");
            }

            validate(chain);
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

            Vault vault = new Vault(new VaultConfig()
                    .address(VAULT_ADDR)
                    .sslConfig(new SslConfig()
                            .keyStore(keyStore, "")
                            .trustStore(keyStore)
                            .build())
                    .build());

            vault.auth().loginByCert();

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
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException{
        LOGGER.debug("Validating server {}, {}", chain, authType);

        Validate.notEmpty(chain);

        Collection<List<?>> sans = chain[0].getSubjectAlternativeNames();
        String clientId = getSpiffeId(sans);
        if (clientId != null) {
            validate(chain);
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

    private void validate(X509Certificate[] chain) throws CertificateException {
        PKIXParameters pkixParameters = getPkixParameters(getRootCA());
        CertPath certPath = getX509CertFactory().generateCertPath(asList(chain));

        try {
            getCertPathValidator().validate(certPath, pkixParameters);
        } catch (CertPathValidatorException | InvalidAlgorithmParameterException e) {
            throw new CertificateException(e);
        }
    }

    private X509Certificate getRootCA() {
        List<X509Certificate> caCertChain = bundleSupplier.get().getCaCertChain();
        return caCertChain.get(caCertChain.size() - 1);
    }
}
