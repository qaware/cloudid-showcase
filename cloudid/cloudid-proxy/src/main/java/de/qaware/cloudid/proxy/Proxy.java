package de.qaware.cloudid.proxy;

import de.qaware.cloudid.lib.CloudId;
import de.qaware.cloudid.lib.IdManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.InputStreamEntity;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Simple proxy controller. Performs some requests against a configurable backend.
 */
@Slf4j
@Controller
@ConfigurationProperties
@RequiredArgsConstructor
public class Proxy {

    private static final String TRACE_HEADER_NAME = "X-SPIFFE-Callstack";
    private final AppProperties appProperties;
    private final HttpClient httpClient;
    private final IdManager idManager = CloudId.getIdManager();

    /**
     * Forwards a request 1:1 to the target defined in {@code de.qaware.cloud.id.demoserver.backend}.
     *
     * @param request request
     * @return response entity
     * @throws IOException if a transmission error occurs
     */
    @RequestMapping("/**")
    public ResponseEntity forwardRequest(HttpServletRequest request) throws IOException {
        HttpResponse response = httpClient.execute(buildBackendRequest(request));

        LOGGER.info("Request was successful: {}", response.getStatusLine());
        return ResponseEntity.status(response.getStatusLine()
                .getStatusCode())
                .headers(convertResponseHeaders(response))
                .body(new InputStreamResource(response.getEntity().getContent()));
    }

    private HttpUriRequest buildBackendRequest(HttpServletRequest request) throws IOException {
        RequestBuilder requestBuilder = RequestBuilder.create(request.getMethod());

        requestBuilder.setUri(appProperties.getBackend() + '/' + request.getServletPath())
                .setEntity(new InputStreamEntity(request.getInputStream()));

        addTraceHeader(request, requestBuilder, idManager);

        HttpUriRequest result = requestBuilder.build();
        LOGGER.info("Proxy request {} to {}", request.getServletPath(), result);
        return result;
    }

    private static void addTraceHeader(HttpServletRequest request, RequestBuilder requestBuilder, IdManager idManager) {
        Enumeration<String> headers = request.getHeaders(TRACE_HEADER_NAME);
        String traceHeaderValue;
        if (headers == null || !headers.hasMoreElements()) {
            LOGGER.debug("No demo-trace header found in request. Creating a new one.");
            traceHeaderValue = idManager.getWorkloadId().getSpiffeId() + "#";
        } else {
            String oldHeader = headers.nextElement();
            LOGGER.debug("Received demo-trace header with content: {}", oldHeader);
            traceHeaderValue = oldHeader + idManager.getWorkloadId().getSpiffeId() + "#";
        }
        LOGGER.debug("Adding header with name {} and value {} to forwarded request", TRACE_HEADER_NAME, traceHeaderValue);
        requestBuilder.addHeader(TRACE_HEADER_NAME, traceHeaderValue);
    }

    private HttpHeaders convertResponseHeaders(HttpResponse response) {
        HttpHeaders responseHeaders = new HttpHeaders();

        for (Header header : response.getAllHeaders()) {
            for (HeaderElement headerElement : header.getElements()) {
                responseHeaders.add(header.getName(), headerElement.getValue());
            }
        }
        return responseHeaders;
    }
}
