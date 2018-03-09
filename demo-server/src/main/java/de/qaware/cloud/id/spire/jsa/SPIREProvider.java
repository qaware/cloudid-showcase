package de.qaware.cloud.id.spire.jsa;

import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.net.ssl.KeyManagerFactory;
import java.security.Provider;
import java.security.Security;

/**
 * Java Security API provider backed by SPIRE.
 */
@Slf4j
@EqualsAndHashCode(doNotUseGetters = true, callSuper = true, exclude = "defaultAlgorithm")
public class SPIREProvider extends Provider {

    /**
     * Algorithm name.
     */
    public static final String ALGORITHM = "SPIRE";

    /**
     * Fixed alias to allow static initialization.
     */
    public static final String ALIAS = "spiffe";

    private static final String NAME = "spiffe-provider";
    private static final double VERSION = 0.1;
    private static final String DESCRIPTION = "";

    private static final String KEY_MANAGER_FACTORY_PREFIX = "KeyManagerFactory.";
    private static final String TRUST_MANAGER_FACTORY_PREFIX = "TrustManagerFactory.";
    private static final String KEY_MANAGER_ALGORITHM_PROPERTY = "ssl.KeyManagerFactory.algorithm";
    private static final String KEY_STORE_PREFIX = "KeyStore.";

    private static final long serialVersionUID = 0L;

    private String defaultAlgorithm;

    /**
     * Constructor.
     */
    public SPIREProvider() {
        super(NAME, VERSION, DESCRIPTION);

        super.put(KEY_MANAGER_FACTORY_PREFIX + ALGORITHM, SPIREKeyManagerFactory.class.getName());
        //super.put(TRUST_MANAGER_FACTORY_PREFIX + ALGORITHM, SPIRETrustManagerFactory.class.getName());
        //super.put(TRUST_MANAGER_FACTORY_PREFIX + "PKIX", SPIRETrustManagerFactory.class.getName());

        // For Spring Boot / Embedded Tomcat which does not use the key manager factory
        super.put(KEY_STORE_PREFIX + ALGORITHM, SPIREKeyStore.class.getName());

    }

    /**
     * Install this provider.
     */
    public void install() {
        if (Security.getProvider(NAME) == null) {
            // LOGGER.info("Installing SPIRE provider at the first position");
            // TODO: Review whether inserting at the first position is a good idea
            //Security.insertProviderAt(new SPIREProvider(), 1);
            Security.addProvider(new BouncyCastleProvider());
            Security.addProvider(new SPIREProvider());

            // Security.insertProviderAt(new BouncyCastleProvider(), 2);
        }

    }

    /**
     * Install this provider as default.
     * <p>
     * Replace the default key manager algorithm with {@link #ALGORITHM}.
     */
    public void installAsDefault() {
        install();

        defaultAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
        Security.setProperty(KEY_MANAGER_ALGORITHM_PROPERTY, ALGORITHM);
    }

    /**
     * Uninstall this provider.
     */
    public void uninstall() {
        if (Security.getProvider(NAME) != null) {
            Security.removeProvider(NAME);
        }

        if (defaultAlgorithm != null && ALGORITHM.equals(Security.getProperty(KEY_MANAGER_ALGORITHM_PROPERTY))) {
            Security.setProperty(KEY_MANAGER_ALGORITHM_PROPERTY, defaultAlgorithm);
        }
    }

    @Override
    public synchronized Service getService(String type, String algorithm) {
        // Trace service lookups
        Service service = super.getService(type, algorithm);
        LOGGER.trace("getService {}.{} = {}", type, algorithm, service);
        return service;
    }

}
