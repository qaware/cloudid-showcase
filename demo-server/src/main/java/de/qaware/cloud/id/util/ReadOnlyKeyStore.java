package de.qaware.cloud.id.util;

import java.io.OutputStream;
import java.security.Key;
import java.security.KeyStoreSpi;
import java.security.cert.Certificate;

/**
 * Superlass for read-only keystores.
 * <p>
 * Will throw an {@link UnsupportedOperationException} on all write operations.
 */
public abstract class ReadOnlyKeyStore extends KeyStoreSpi {

    @Override
    public void engineSetKeyEntry(String alias, Key key, char[] password, Certificate[] chain) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void engineSetKeyEntry(String alias, byte[] key, Certificate[] chain) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void engineSetCertificateEntry(String alias, Certificate cert) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void engineDeleteEntry(String alias) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void engineStore(OutputStream stream, char[] password) {
        throw new UnsupportedOperationException();
    }

}
