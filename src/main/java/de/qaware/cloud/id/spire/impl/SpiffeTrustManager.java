package de.qaware.cloud.id.spire.impl;

import de.qaware.cloud.id.spire.SVIDBundle;
import lombok.RequiredArgsConstructor;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.function.Supplier;

/**
 * X.509 trust manager backed by a SPIFFE identity.
 */
@RequiredArgsConstructor
public class SpiffeTrustManager implements X509TrustManager {

    private final Supplier<SVIDBundle> bundleSupplier;


    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        // TODO: Implement
    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        // TODO: Implement
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[]{bundleSupplier.get().getCertChain().get(0)};
    }

}
