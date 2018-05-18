package de.qaware.cloudid.proxy

import de.qaware.cloudid.lib.ACL
import de.qaware.cloudid.lib.ACLManager

import java.util.function.Consumer

class TestACLManager implements ACLManager {
    def aclEntries = new HashSet<ACL.Entry>()
    def acl = new ACL(aclEntries)

    @Override
    ACL get() {
        return acl
    }

    @Override
    void addListener(Consumer<ACL> listener) {
        listener.accept(acl)
    }

    @Override
    void start() {

    }

    @Override
    void stop() {

    }
}
