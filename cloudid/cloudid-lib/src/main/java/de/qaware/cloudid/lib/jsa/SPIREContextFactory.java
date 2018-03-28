package de.qaware.cloudid.lib.jsa;

import lombok.experimental.UtilityClass;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * SSL context factory.
 */
@UtilityClass
public class SPIREContextFactory {

    /**
     * Create a SSL context backed by the SPIRE provider
     *
     * @return SSL context
     */
    public static SSLContext get() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(
                    new SPIREKeyManagerFactory().engineGetKeyManagers(),
                    new SPIRETrustManagerFactory().engineGetTrustManagers(),
                    null);
            return sslContext;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new IllegalStateException(e);
        }
    }

}
