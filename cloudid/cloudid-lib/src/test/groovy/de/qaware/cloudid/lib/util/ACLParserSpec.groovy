package de.qaware.cloudid.lib.util

import spock.lang.Specification

/**
 * Specification for {@link ACLParserSpec}.
 */
class ACLParserSpec extends Specification {
    def 'isClientAllowed'() {
        given:
        String aclString = "spiffe://trust-domain/client -> spiffe://trust-domain/foo" +
                "\nspiffe://trust-domain/target -> spiffe://trust-domain/foo" +
                "\nspiffe://trust-domain/target -> spiffe://trust-domain/client" +
                "\nspiffe://trust-domain/client -> spiffe://trust-domain/target" +
                "\nspiffe://trust-domain/path/test -> spiffe://trust-domain/target"
        String clientPermitted = "spiffe://trust-domain/client"
        String clientForbidden = "spiffe://trust-domain/foo"
        String target = "spiffe://trust-domain/target"
        String targetNotMatching = "spiffe://trust-domain/path/test"
        String targetNotExisting = "spiffe://example.com/abcd"

        when:
        boolean clientToTarget = ACLParser.isClientAllowed(aclString, clientPermitted, target)
        boolean clientForbiddenToTarget = ACLParser.isClientAllowed(aclString, clientForbidden, target)
        boolean clientPermittedToTargetNotMatching = ACLParser.isClientAllowed(aclString, clientPermitted, targetNotMatching)
        boolean clientPermittedToTargetNotExisting = ACLParser.isClientAllowed(aclString, clientPermitted, targetNotExisting)
        boolean clientForbiddenToTargetNotMatching = ACLParser.isClientAllowed(aclString, clientForbidden, targetNotMatching)

        then:
        clientToTarget
        !clientForbiddenToTarget
        !clientPermittedToTargetNotMatching
        !clientPermittedToTargetNotExisting
        !clientForbiddenToTargetNotMatching
    }
}