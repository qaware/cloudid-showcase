package de.qaware.cloud.id.spire.impl;

import de.qaware.cloud.id.spire.SVIDBundle;

import javax.net.ssl.X509KeyManager;
import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.function.Supplier;

/**
 * X.509 key manager backed by a SPIFFE SVId.
 */
public class SpiffeKeyManager implements X509KeyManager {

    private final Supplier<SVIDBundle> bundleSupplier;

    /**
     * Constructor.
     *
     * @param bundleSupplier bundle supplier
     */
    public SpiffeKeyManager(Supplier<SVIDBundle> bundleSupplier) {
        this.bundleSupplier = bundleSupplier;
    }


    @Override
    public String[] getClientAliases(String s, Principal[] principals) {
        return new String[]{this.bundleSupplier.get().getSvId()};
    }

    @Override
    public String chooseClientAlias(String[] strings, Principal[] principals, Socket socket) {
        return this.bundleSupplier.get().getSvId();
    }

    @Override
    public String[] getServerAliases(String s, Principal[] principals) {
        return new String[]{this.bundleSupplier.get().getSvId()};
    }

    @Override
    public String chooseServerAlias(String s, Principal[] principals, Socket socket) {
        return this.bundleSupplier.get().getSvId();
    }

    @Override
    public X509Certificate[] getCertificateChain(String s) {
        return new X509Certificate[]{bundleSupplier.get().getCertificate()};
    }

    @Override
    public PrivateKey getPrivateKey(String s) {
        return bundleSupplier.get().getKeyPair().getPrivate();
    }


}
