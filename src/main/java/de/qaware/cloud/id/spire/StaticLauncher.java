package de.qaware.cloud.id.spire;

import de.qaware.cloud.id.util.Reflection;
import de.qaware.cloud.id.util.config.Props;
import lombok.experimental.UtilityClass;

import java.util.function.Supplier;

/**
 * Launcher starting the SPIRE Agent connection in a static context.
 * <p>
 * Instantiates the bundle supplier factory defined by {@link Config#BUNDLE_SUPPLIER_FACTORY_CLASS}
 * and starts the life cycle.
 */
@UtilityClass
public class StaticLauncher {

    /**
     * Get the bundle supplier, starting the SPIRE Agent connection if necessary.
     *
     * @return bundle supplier
     */
    public static Supplier<Bundle> getBundleSupplier() {
        // Log the configuration on DEBUG
        Props.debugLog(Config.class);

        BundleSupplierFactory factory = Reflection.instantiate(Config.BUNDLE_SUPPLIER_FACTORY_CLASS.get());
        factory.start();
        return factory.get();
    }

}
