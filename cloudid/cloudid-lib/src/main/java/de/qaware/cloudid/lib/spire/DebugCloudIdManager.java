package de.qaware.cloudid.lib.spire;

import de.qaware.cloudid.lib.util.Certificates;
import de.qaware.cloudid.lib.util.config.Prop;
import de.qaware.cloudid.lib.util.config.Props;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import static de.qaware.cloudid.lib.util.Reflection.getContextClassLoader;
import static de.qaware.cloudid.lib.util.config.Props.stringOf;
import static java.text.MessageFormat.format;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

/**
 * Debug CloudId Manager.
 */
public class DebugCloudIdManager implements CloudIdManager {

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


    private static final String CLASSPATH_PREFIX = "classpath:";

    private final Collection<Consumer<Bundles>> listeners = new ArrayList<>();
    private Bundles bundles;


    @Override
    public synchronized void start() {
        Props.debugLog(getClass());

        try {
            bundles = new Bundles(singletonList(loadBundle()), Instant.MAX);
            listeners.forEach(l -> l.accept(bundles));
        } catch (IOException | GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public synchronized void stop() {
        bundles = null;
    }

    @Override
    public synchronized Bundles getBundles() {
        return bundles;
    }

    @Override
    public synchronized void addListener(Consumer<Bundles> listener) {
        listeners.add(listener);
        listener.accept(bundles);
    }


    private static Bundle loadBundle() throws IOException, GeneralSecurityException {
        KeyStore keystore = loadKeyStore();

        String alias = KEYSTORE_ALIAS.get();

        PrivateKey privateKey = getPrivateKey(keystore, alias);
        X509Certificate certificate = getCertificate(keystore, alias);
        List<X509Certificate> caCertChain = getCaCertChain(keystore, alias);
        String spiffeId = Certificates.getSpiffeId(certificate).orElseThrow(IllegalStateException::new);

        return new Bundle(
                spiffeId,
                certificate,
                new KeyPair(certificate.getPublicKey(), privateKey),
                caCertChain,
                emptyMap());
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
            InputStream inputStream = getContextClassLoader().getResourceAsStream(location.substring(CLASSPATH_PREFIX.length()));
            if (inputStream == null) {
                throw new IllegalArgumentException(format("Unable to load keystore {0}", location));
            }
            return inputStream;
        } else {
            return new FileInputStream(location);
        }
    }
}
