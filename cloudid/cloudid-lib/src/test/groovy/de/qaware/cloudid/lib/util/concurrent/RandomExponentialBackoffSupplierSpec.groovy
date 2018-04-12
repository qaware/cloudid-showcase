package de.qaware.cloudid.lib.util.concurrent

import spock.lang.Specification

import java.time.Duration
import java.util.function.Supplier

/**
 * Specification for {@link RandomExponentialBackoffSupplier}
 */
class RandomExponentialBackoffSupplierSpec extends Specification {

    def 'retry works'() {
        when:
        def result = new Object()
        def sourceSupplier = Mock(Supplier) {
            get() >> { throw new RuntimeException('first request failed') } >> result
        }

        def supplier = new RandomExponentialBackoffSupplier(sourceSupplier, 0, Duration.ofMillis(0), 0)

        then:
        supplier.get().is(result)
    }

}
