package de.qaware.cloud.id.spire;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedTrustManager;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.cert.*;
import java.util.function.Supplier;

import static de.qaware.cloud.id.util.Certificates.*;
import static java.util.Arrays.asList;

/**
 * X.509 trust manager backed by SPIRE bundles.
 */
@Slf4j
@RequiredArgsConstructor
public class SPIRETrustManager extends X509ExtendedTrustManager {

    private final Supplier<SVIDBundle> bundleSupplier;

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        LOGGER.debug("No accepted issuers");
        return new X509Certificate[0];
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        LOGGER.debug("Validating client {}, {}", chain, authType);

        LOGGER.warn("Blindly trusting client");
        // TODO: Provide proper long-lived test certificates
        // validate(chain);
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

        LOGGER.warn("Blindly trusting server");
        // TODO: Provide proper long-lived test certificates
        // validate(chain);
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
        PKIXParameters pkixParameters = getPkixParameters(bundleSupplier.get().getCaCertChain().get(0));
        CertPath certPath = getX509CertFactory().generateCertPath(asList(chain));

        try {
            getCertPathValidator().validate(certPath, pkixParameters);
        } catch (CertPathValidatorException | InvalidAlgorithmParameterException e) {
            throw new CertificateException(e);
        }
    }

}
