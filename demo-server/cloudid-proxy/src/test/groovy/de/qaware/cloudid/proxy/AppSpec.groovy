package de.qaware.cloudid.proxy

import de.qaware.cloudid.lib.jsa.SPIREProvider
import de.qaware.cloudid.lib.spire.DebugBundleSupplierFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import spock.lang.Specification
import spock.util.environment.RestoreSystemProperties

import static de.qaware.cloudid.lib.spire.Config.BUNDLE_SUPPLIER_FACTORY_CLASS
import static de.qaware.cloudid.lib.spire.DebugBundleSupplierFactory.KEYSTORE_LOCATION

/**
 * Specification for the Spring Boot applicaton
 *
 */
@RestoreSystemProperties
@SpringBootTest
class AppSpec extends Specification {

    @Autowired
    ApplicationContext context

    void setupSpec() {
        System.setProperty(BUNDLE_SUPPLIER_FACTORY_CLASS.getSysProp(),
                DebugBundleSupplierFactory.class.getName())
        System.setProperty(KEYSTORE_LOCATION.getSysProp(), TestResources.testKeystoreLocation)

        SPIREProvider.install()
    }

    void cleanupSpec() {
        SPIREProvider.uninstall()
    }

    def "context loads"() {
        expect:
        context.getBean(DemoServerApplication) != null
        context.getBean(Proxy) != null
    }

}