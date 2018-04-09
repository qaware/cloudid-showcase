package de.qaware.cloudid.lib.spire;

import de.qaware.cloudid.lib.util.config.Prop;
import de.qaware.cloudid.lib.util.config.Props;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.function.Supplier;

import static de.qaware.cloudid.lib.util.Reflection.getContextClassLoader;
import static de.qaware.cloudid.lib.util.config.Props.stringOf;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.startsWith;

/**
 * Bundle supplier factory for debugging backed by a local key store.
 */
@SuppressWarnings("unused" /* Referenced by system properties */)
@Slf4j
public class DebugBundleSupplierFactory implements BundleSupplierFactory {

    /**
     * Keystore location.
     */
    public static final Prop<String> KEYSTORE_LOCATION = stringOf("spire.debug.keystore.location", "spire-debug.jks");
    /**
     * Keystore type.
     */
    public static final Prop<String> KEYSTORE_TYPE = stringOf("spire.debug.keystore.type", "jks");
    /**
     * Keystore password.
     */
    public static final Prop<String> KEYSTORE_PASSWORD = stringOf("spire.debug.keystore.password", "");
    /**
     * Keystore alias.
     */
    public static final Prop<String> KEYSTORE_ALIAS = stringOf("spire.debug.keystore.alias", "spiffe");
    /**
     * Key password.
     */
    public static final Prop<String> KEY_PASSWORD = stringOf("spire.debug.key.password", "");

    /**
     * Id of an URI subject alternative name.
     */
    private static final int SAN_URI_OID = 6;
    /**
     * Index of the object Id field.
     */
    private static final int SAN_OID_I = 0;
    /**
     * Index of the value field.
     */
    private static final int SAN_VALUE_I = 1;

    private static final String SPIFFE_URI_PREFIX = "spiffe://";
    private static final String CLASSPATH_PREFIX = "classpath:";

    /**
     * Constructor.
     */
    public DebugBundleSupplierFactory() {
        Props.debugLog(getClass());
    }

    @Override
    public Supplier<Bundle> get() {
        try {
            Bundle bundle = loadBundle();
            return () -> bundle;
        } catch (IOException | GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Bundle loadBundle() throws IOException, GeneralSecurityException {
        KeyStore keystore = loadKeyStore();

        String alias = KEYSTORE_ALIAS.get();

        PrivateKey privateKey = getPrivateKey(keystore, alias);
        X509Certificate certificate = getCertificate(keystore, alias);
        List<X509Certificate> caCertChain = getCaCertChain(keystore, alias);

        return new Bundle(
                getSpiffeId(certificate),
                certificate,
                new KeyPair(certificate.getPublicKey(), privateKey),
                caCertChain,
                emptyMap());
    }

    private static String getSpiffeId(X509Certificate certificate) throws GeneralSecurityException {
        return certificate.getSubjectAlternativeNames().stream()
                .filter(san -> ((Integer) san.get(SAN_OID_I) == SAN_URI_OID))
                .map(san -> (String) san.get(SAN_VALUE_I))
                .filter(uri -> startsWith(uri, SPIFFE_URI_PREFIX))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        format("No SPIFFE Id SAN found in certificate %s", certificate)));
    }

    private static PrivateKey getPrivateKey(KeyStore keystore, String alias) throws GeneralSecurityException {
        return (PrivateKey) keystore.getKey(alias, KEY_PASSWORD.get().toCharArray());
    }

    private static X509Certificate getCertificate(KeyStore keystore, String alias) throws KeyStoreException {
        return (X509Certificate) keystore.getCertificate(alias);
    }

    @SuppressWarnings("unchecked")
    private static List<X509Certificate> getCaCertChain(KeyStore keystore, String alias) throws KeyStoreException {
        return stream(keystore.getCertificateChain(alias))
                .map(c -> (X509Certificate) c)
                .collect(toList());
    }

    private static KeyStore loadKeyStore() throws IOException, GeneralSecurityException {
        KeyStore keystore;

        try (InputStream is = open(KEYSTORE_LOCATION.get())) {
            keystore = KeyStore.getInstance(KEYSTORE_TYPE.get());
            keystore.load(is, KEYSTORE_PASSWORD.get().toCharArray());
        }
        return keystore;
    }

    private static InputStream open(String location) throws FileNotFoundException {
        if (location.startsWith(CLASSPATH_PREFIX)) {
            return getContextClassLoader().getResourceAsStream(location.substring(CLASSPATH_PREFIX.length()));
        } else {
            return new FileInputStream(location);
        }
    }

}
