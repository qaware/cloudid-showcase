package de.qaware.cloudid.lib.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@UtilityClass
public class ACLParser {

    private static final String ENTRY_SEPARATOR = "\n";
    private static final String SOURCE_TARGET_SEPARATOR = " -> ";

    public static boolean isClientAllowed(String aclString, String clientId, String targetId) {
        LOGGER.trace("{} {} {}", aclString, clientId, targetId);
        return getAllowedClients(aclString, targetId).contains(clientId);
    }

    private static List<String> getAllowedClients(String aclString, String target) {
        Set<String> allowed = new HashSet<>();
        LOGGER.debug("Checking ACL: {}", aclString);
        for (String sub : aclString.split(ENTRY_SEPARATOR)) {
            LOGGER.trace("Checking Access Control String: {}", sub);
            String[] parts = sub.split(SOURCE_TARGET_SEPARATOR);
            if (parts.length != 2) {
                LOGGER.warn("ACL substring in unexpected format. Ignoring substring. Substring: {}", sub);
                continue;
            }
            // trim to remove leading/following whitespaces
            if (parts[1].trim().equals(target)) {
                allowed.add(parts[0].trim());
            }
        }
        LOGGER.debug("Allowed clients for target {}: {}", target, allowed);
        return new ArrayList<>(allowed);
    }
}
