package de.qaware.cloudid.lib.jsa;

import de.qaware.cloudid.lib.CloudId;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.KeyManagerFactory;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.security.Provider;
import java.security.Security;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

/**
 * JSA provider backed by CloudId.
 */
@Slf4j
@EqualsAndHashCode(doNotUseGetters = true, callSuper = true)
public class CloudIdProvider extends Provider {

    private static final String KEY_MANAGER_FACTORY_PREFIX = "KeyManagerFactory.";
    private static final String TRUST_MANAGER_FACTORY_PREFIX = "TrustManagerFactory.";
    private static final String KEY_STORE_PREFIX = "KeyStore.";

    private static final long serialVersionUID = 0L;

    private static String defaultKeyManagerFactoryAlgorithm;


    /**
     * Constructor.
     */
    @SuppressWarnings("deprecation" /* Required for Java 8 compatibility */)
    public CloudIdProvider() {
        super(CloudId.PROVIDER_NAME, CloudId.PROVIDER_VERSION, CloudId.PROVIDER_DESCRIPTION);

        // Custom key manager
        super.put(KEY_MANAGER_FACTORY_PREFIX + CloudId.ALGORITHM, CloudIdKeyManagerFactory.class.getName());

        // Custom trust manager
        super.put(TRUST_MANAGER_FACTORY_PREFIX + CloudId.ALGORITHM, CloudIdTrustManagerFactory.class.getName());
        super.put(TRUST_MANAGER_FACTORY_PREFIX + "PKIX", CloudIdTrustManagerFactory.class.getName());

        // Custom key store
        super.put(KEY_STORE_PREFIX + CloudId.ALGORITHM, CloudIdKeyStore.class.getName());
        super.put(KEY_STORE_PREFIX + "SunX509", CloudIdKeyStore.class.getName());

        // Custom trust store
        super.put(KEY_STORE_PREFIX + CloudId.TRUST_STORE_ALGORITHM, CloudIdTrustStore.class.getName());

    }

    /**
     * Install this provider.
     */
    public static synchronized void install() {
        if (Security.getProvider(CloudId.PROVIDER_NAME) == null) {

            // Install the Key Manager Factory as default
            defaultKeyManagerFactoryAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
            Security.setProperty("ssl.KeyManagerFactory.algorithm", CloudId.ALGORITHM);

            // Install the provider at the first position
            Security.insertProviderAt(new CloudIdProvider(), 1);

            logJVM();
            logProviders();
        }

    }

    /**
     * Uninstall this provider.
     */
    public static synchronized void uninstall() {
        if (Security.getProvider(CloudId.PROVIDER_NAME) != null) {
            Security.setProperty("ssl.KeyManagerFactory.algorithm", defaultKeyManagerFactoryAlgorithm);

            Security.removeProvider(CloudId.PROVIDER_NAME);
        }
    }

    @Override
    public synchronized Service getService(String type, String algorithm) {
        // Trace service lookups
        Service service = super.getService(type, algorithm);
        LOGGER.trace("getService {}.{} = {}", type, algorithm, service);
        return service;
    }


    private static void logJVM() {
        if (LOGGER.isDebugEnabled()) {
            RuntimeMXBean mxBean = ManagementFactory.getRuntimeMXBean();
            LOGGER.debug("{} {} {}",
                    mxBean.getVmVendor(),
                    mxBean.getVmName(),
                    mxBean.getVmVersion()
            );
        }
    }

    private static void logProviders() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Java Security Providers: {}", stream(Security.getProviders())
                    .map(Provider::getName)
                    .collect(joining(", ")));
        }
    }

}
