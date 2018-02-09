package de.qaware.cloud.id.spire.impl;

import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.*;
import java.net.Socket;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Slf4j
public class SpiffeTrustManagerFactory extends TrustManagerFactorySpi {

    @Override
    protected void engineInit(KeyStore keyStore) {
        LOGGER.error("Delegating to a keystore backed key manager is not supported yet");
    }

    @Override
    protected void engineInit(ManagerFactoryParameters managerFactoryParameters) {
        LOGGER.error("Delegating to a keystore backed key manager is not supported yet");
    }

    @Override
    protected TrustManager[] engineGetTrustManagers() {
        return new TrustManager[]{
                new X509ExtendedTrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                        LOGGER.warn("Trusting {}, {}", s, x509Certificates);
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                        LOGGER.warn("Trusting {}, {}", s, x509Certificates);
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        LOGGER.warn("No accepted issuers");
                        return new X509Certificate[0];
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s, Socket socket) {
                        LOGGER.warn("Trusting {}, {}", s, x509Certificates);
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s, Socket socket) {
                        LOGGER.warn("Trusting {}, {}", s, x509Certificates);
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) {
                        LOGGER.warn("Trusting {}, {}", s, x509Certificates);
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) {
                        LOGGER.warn("Trusting {}, {}", s, x509Certificates);
                    }
                }
        };
    }
}
