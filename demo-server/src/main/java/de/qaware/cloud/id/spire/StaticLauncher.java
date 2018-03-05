package de.qaware.cloud.id.spire;

import de.qaware.cloud.id.util.Reflection;
import de.qaware.cloud.id.util.config.Props;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * Launcher starting the SPIRE Agent connection in a static context.
 * <p>
 * Instantiates the bundle supplier factory defined by {@link Config#BUNDLE_SUPPLIER_FACTORY_CLASS}
 * and starts the life cycle.
 */
@Slf4j
@UtilityClass
public class StaticLauncher {

    private static BundleSupplierFactory bundleSupplierFactory;

    /**
     * Get the bundle supplier, starting the SPIRE Agent connection if necessary.
     *
     * @return bundle supplier
     */
    public static synchronized Supplier<Bundle> getBundleSupplier() {
        if (bundleSupplierFactory == null) {
            // Log the configuration on DEBUG
            Props.debugLog(Config.class);

            try {
                bundleSupplierFactory = Reflection.instantiate(Config.BUNDLE_SUPPLIER_FACTORY_CLASS.get());
            } catch (IllegalArgumentException e) {
                // Log the exception here as it might get dropped later on by JSA
                LOGGER.error("Error instantiating the bundle supplier factory", e);
                throw e;
            }

            bundleSupplierFactory.start();
        }

        return bundleSupplierFactory.get();
    }

}
