package de.qaware.cloud.id.spire.impl;

import javax.net.ssl.KeyManagerFactory;
import java.security.Provider;
import java.security.Security;

public class SpiffeProvider extends Provider {

    public static final String NAME = "spiffe-provider";
    public static final double VERSION = 0.1;
    public static final String DESCRIPTION = "";

    public static final String ALGORITHM = "spiffe";

    private static final String KEY_MANAGER_FACTORY_PREFIX = "KeyManagerFactory.";
    private static final String KEY_MANAGER_ALGORITHM_PROPERTY = "ssl.KeyManagerFactory.algorithm";

    private String defaultAlgorithm;

    public SpiffeProvider() {
        super(NAME, VERSION, DESCRIPTION);
        put(KEY_MANAGER_FACTORY_PREFIX + ALGORITHM, SpiffeKeyManagerFactory.class.getName());
    }

    public void install(boolean asDefault) {
        if (Security.getProvider(NAME) == null) {
            Security.addProvider(new SpiffeProvider());
        }

        if (asDefault) {
            defaultAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
            Security.setProperty(KEY_MANAGER_ALGORITHM_PROPERTY, ALGORITHM);
        }
    }

    public void uninstall() {
        if (Security.getProvider(NAME) != null) {
            Security.removeProvider(NAME);
        }

        if (defaultAlgorithm != null && ALGORITHM.equals(Security.getProperty(KEY_MANAGER_ALGORITHM_PROPERTY))) {
            Security.setProperty(KEY_MANAGER_ALGORITHM_PROPERTY, defaultAlgorithm);
        }
    }

}
