package de.qaware.cloud.id.spire.jsa;

import de.qaware.cloud.id.spire.Bundle;
import lombok.experimental.UtilityClass;

import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.stream.Stream;

import static java.util.Collections.singleton;

/**
 * Utilities for the SPIRE JSA adapter.
 */
@UtilityClass
class SPIREJSAUtils {

    /**
     * Get the certificate chain in JSA format (starting with the leaf certificate) from a bundle.
     *
     * @param bundle bundle
     * @return certificate chain
     */
    static X509Certificate[] getCertChain(Bundle bundle) {
        return Stream.of(singleton(bundle.getCertificate()), bundle.getCaCertChain())
                .flatMap(Collection::stream)
                .toArray(X509Certificate[]::new);
    }

}
