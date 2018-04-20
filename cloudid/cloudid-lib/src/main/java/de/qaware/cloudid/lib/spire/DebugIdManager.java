package de.qaware.cloudid.lib.spire;

import de.qaware.cloudid.lib.Config;
import de.qaware.cloudid.lib.IdManager;
import de.qaware.cloudid.lib.WorkloadId;
import de.qaware.cloudid.lib.WorkloadIds;
import de.qaware.cloudid.util.Certificates;
import de.qaware.cloudid.util.config.Props;

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

import static de.qaware.cloudid.util.Reflection.getContextClassLoader;
import static java.text.MessageFormat.format;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

/**
 * Debug Id Manager.
 */
public class DebugIdManager implements IdManager {


    private static final String CLASSPATH_PREFIX = "classpath:";

    private final Collection<Consumer<WorkloadIds>> listeners = new ArrayList<>();
    private WorkloadIds workloadIds;


    @Override
    public synchronized void start() {
        Props.debugLog(getClass());

        try {
            workloadIds = new WorkloadIds(singletonList(loadBundle()), Instant.MAX);
            listeners.forEach(l -> l.accept(workloadIds));
        } catch (IOException | GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public synchronized void stop() {
        workloadIds = null;
    }

    @Override
    public synchronized WorkloadIds get() {
        return workloadIds;
    }

    @Override
    public synchronized void addListener(Consumer<WorkloadIds> listener) {
        listeners.add(listener);
        listener.accept(workloadIds);
    }


    private static WorkloadId loadBundle() throws IOException, GeneralSecurityException {
        KeyStore keystore = loadKeyStore();

        String alias = Config.DEBUG_KEYSTORE_ALIAS.get();

        PrivateKey privateKey = getPrivateKey(keystore, alias);
        X509Certificate certificate = getCertificate(keystore, alias);
        List<X509Certificate> caCertChain = getCaCertChain(keystore, alias);
        String spiffeId = Certificates.getSpiffeId(certificate).orElseThrow(IllegalStateException::new);

        return new WorkloadId(
                spiffeId,
                certificate,
                new KeyPair(certificate.getPublicKey(), privateKey),
                caCertChain,
                emptyMap());
    }

    private static PrivateKey getPrivateKey(KeyStore keystore, String alias) throws GeneralSecurityException {
        return (PrivateKey) keystore.getKey(alias, Config.DEBUG_KEY_PASSWORD.get().toCharArray());
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

        try (InputStream is = open(Config.DEBUG_KEYSTORE_LOCATION.get())) {
            keystore = KeyStore.getInstance(Config.DEBUG_KEYSTORE_TYPE.get());
            keystore.load(is, Config.DEBUG_KEYSTORE_PASSWORD.get().toCharArray());
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
