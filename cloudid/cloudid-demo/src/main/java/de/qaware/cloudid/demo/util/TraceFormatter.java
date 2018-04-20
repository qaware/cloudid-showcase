package de.qaware.cloudid.demo.util;

import de.qaware.cloudid.lib.CloudId;
import de.qaware.cloudid.lib.IdManager;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.splitByWholeSeparator;

/**
 * Utility class for retrieving and formatting a SPIFFE call trace
 */
@UtilityClass
@Slf4j
public class TraceFormatter {

    private static final String TRACE_SEPARATOR = "#";
    private static final IdManager idManager = CloudId.getIdManager();

    /**
     * Returns the callstack trace contained in request if possible.
     * @param request request containing header with SPIFFE callstack
     * @return list calls with first entry being the first call and the last our own id
     */
    public static List<String> getFormattedTrace(HttpServletRequest request) {
        List<String> trace;
        Enumeration<String> headers = request.getHeaders("X-SPIFFE-Callstack");
        if (headers.hasMoreElements()) {
            String raw = headers.nextElement();
            LOGGER.debug("Received SPIFFE callstack: {}", raw);
            trace = formatTrace(raw);
        } else {
            LOGGER.debug("Received empty SPIFFE callstack");
            trace = new ArrayList<>();
            trace.add("No trace available!");
        }
        return trace;
    }

    private static List<String> splitTrace(String trace) {
        // Remove last TRACE_SEPARATOR which is the last letter of the string
        trace = trace.trim();
        trace =trace.substring(0, trace.length() - 1);
        return Arrays.asList(splitByWholeSeparator(trace, TRACE_SEPARATOR));
    }

    private static List<String> formatTrace(String raw) {
        assert raw != null;
        List<String> trace = new ArrayList<>(splitTrace(raw));
        trace.add(idManager.getWorkloadId().getSpiffeId());
        return trace;
    }
}
