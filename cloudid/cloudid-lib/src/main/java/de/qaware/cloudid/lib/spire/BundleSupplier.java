package de.qaware.cloudid.lib.spire;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.function.Supplier;

import static java.time.Instant.now;

/**
 * Provides up-to-date bundles.
 * <p>
 * Updates the backing bundles according to the expiry of the last bundle.
 * Selects the bundle with the max. time to life while skipping bundles that have not yet become valid.
 */
@SuppressWarnings("squid:S2142" /* Rule is broken as it cannot handle interrupt handling in methods */)
@Slf4j
@RequiredArgsConstructor
class BundleSupplier implements Supplier<Bundle> {
    private final Supplier<Bundles> bundlesSupplier;

    /**
     * Get the bundle.
     *
     * @return bundle
     */
    @Override
    public Bundle get() {
        // This selects the first tuple with that is valid, preferring tuples with longer validity.
        // Bundles are sorted descending by notAfter.
        Instant now = now();
        return bundlesSupplier.get().getBundleList().stream()
                .filter(b -> b.getNotBefore().isBefore(now))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

}
