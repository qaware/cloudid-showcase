package de.qaware.cloudid.demo;

import de.qaware.cloudid.lib.CloudId;
import de.qaware.cloudid.lib.IdManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * Simple proxy controller. Performs some requests against a configurable backend.
 */
@Slf4j
@Controller
@ConfigurationProperties
@RequiredArgsConstructor
public class DemoResponder {

    private final IdManager idManager = CloudId.getIdManager();

    /**
     * Generates a html demo page which contains the SPIFFE callstack, if available.
     *
     * @param path    request path
     * @param request request
     * @return response entity with the generated html page
     */
    @RequestMapping("/{path}")
    public ResponseEntity demoPageRequest(@PathVariable String path, HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaders("X-SPIFFE-Callstack");
        String trace;
        if (headers.hasMoreElements()) {
            trace = headers.nextElement();
            LOGGER.debug("Received SPIFFE callstack: {}", trace);
            trace = formatTrace(trace, true);
        } else {
            LOGGER.debug("Received empty SPIFFE callstack");
            trace = "NO TRACE AVAILABLE!";
        }
        String responseBody = generateResponseBody(trace);
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseBody);
    }

    private String generateResponseBody(String trace) {
        String ourId = idManager.getSingleBundle().getSpiffeId();
        String body = getBodyTemplate();
        body = body.replace("%TRACE", trace);
        body = body.replace("%MY_IDENTITY", ourId);
        return body;
    }

    private String getBodyTemplate() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "<meta charset=\"UTF-8\">\n" +
                "<title>CNI Demo</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h2>My identity:</h2>\n" +
                "%MY_IDENTITY\n" +
                "<h2>Call trace:</h2><br />\n" +
                "%TRACE\n" +
                "</body>\n" +
                "</html>";
    }

    private String formatTrace(String trace, boolean appendOwnId) {
        assert trace != null;
        if (appendOwnId) {
            trace += idManager.getSingleBundle().getSpiffeId();
        }
        String[] traces = trace.split("#");
        StringBuilder traceBuilder = new StringBuilder();
        traceBuilder.append("<ol>");
        for (String currentTrace : traces) {
            traceBuilder.append("<li>").append(currentTrace).append("</li><br />");
        }
        traceBuilder.append("</ol>");
        return traceBuilder.toString();
    }
}
