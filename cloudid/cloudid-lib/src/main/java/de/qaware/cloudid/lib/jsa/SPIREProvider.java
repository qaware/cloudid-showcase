package de.qaware.cloudid.lib.jsa;

import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.security.Provider;
import java.security.Security;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

/**
 * Java Security API provider backed by SPIRE.
 */
@Slf4j
@EqualsAndHashCode(doNotUseGetters = true, callSuper = true)
public class SPIREProvider extends Provider {

    /**
     * Algorithm name.
     */
    public static final String ALGORITHM = "SPIRE";

    /**
     * Fixed alias to allow static initialization.
     */
    public static final String ALIAS = "spiffe";

    /**
     * Provider name.
     */
    public static final String NAME = "spiffe-provider";
    private static final double VERSION = 0.1;
    private static final String DESCRIPTION = "";

    private static final String KEY_MANAGER_FACTORY_PREFIX = "KeyManagerFactory.";
    private static final String TRUST_MANAGER_FACTORY_PREFIX = "TrustManagerFactory.";
    private static final String KEY_STORE_PREFIX = "KeyStore.";

    private static final long serialVersionUID = 0L;


    /**
     * Constructor.
     */
    @SuppressWarnings("deprecation" /* Required for Java 8 compatibility */)
    public SPIREProvider() {
        super(NAME, VERSION, DESCRIPTION);

        super.put(KEY_MANAGER_FACTORY_PREFIX + ALGORITHM, SPIREKeyManagerFactory.class.getName());
        super.put(TRUST_MANAGER_FACTORY_PREFIX + ALGORITHM, SPIRETrustManagerFactory.class.getName());
        super.put(TRUST_MANAGER_FACTORY_PREFIX + "PKIX", SPIRETrustManagerFactory.class.getName());

        super.put(KEY_STORE_PREFIX + ALGORITHM, SPIREKeyStore.class.getName());
        super.put(KEY_STORE_PREFIX + "SunX509", SPIREKeyStore.class.getName());

    }

    /**
     * Install this provider.
     */
    public static void install() {
        if (Security.getProvider(NAME) == null) {
            // Install the provider at the first position
            Security.insertProviderAt(new SPIREProvider(), 1);

            logJVM();
            logProviders();
        }

    }

    /**
     * Uninstall this provider.
     */
    public static void uninstall() {
        if (Security.getProvider(NAME) != null) {
            Security.removeProvider(NAME);
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
