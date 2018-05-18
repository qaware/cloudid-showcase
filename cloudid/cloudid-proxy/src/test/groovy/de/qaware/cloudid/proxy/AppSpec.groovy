package de.qaware.cloudid.proxy

import de.qaware.cloudid.lib.jsa.CloudIdProvider
import de.qaware.cloudid.lib.spire.DebugIdManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import spock.lang.Specification
import spock.util.environment.RestoreSystemProperties

import static de.qaware.cloudid.lib.Config.*

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
        System.setProperty(ID_MANAGER_CLASS.getSysProp(), DebugIdManager.class.getName())
        System.setProperty(DEBUG_KEYSTORE_LOCATION.getSysProp(), TestResources.testKeystoreLocation)
        System.setProperty(ACL_DISABLED.sysProp, true.toString())
        System.setProperty(ACL_MANAGER_CLASS.sysProp, TestACLManager.class.getName())

        CloudIdProvider.install()
    }

    void cleanupSpec() {
        CloudIdProvider.uninstall()
    }

    def "context loads"() {
        expect:
        context.getBean(DemoServerApplication) != null
        context.getBean(Proxy) != null
    }

}
