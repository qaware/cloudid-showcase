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
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
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
     * @param path    request path
     * @param request request
     * @return response entity
     * @throws IOException if a transmission error occurs
     */
    @RequestMapping("/{path}")
    public ResponseEntity forwardRequest(@PathVariable String path, HttpServletRequest request) throws IOException {
        HttpUriRequest backendRequest = buildBackendRequest(path, request);
        LOGGER.info("Proxy request {} to {}", path, backendRequest);
        HttpResponse response = httpClient.execute(backendRequest);

        HttpHeaders responseHeaders = convertResponseHeaders(response);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        response.getEntity().writeTo(outputStream);
        String charsetName = responseHeaders.getContentType() != null ? responseHeaders.getContentType().getCharset().name() : "UTF-8";
        LOGGER.info("Request was successful: {}", response.getStatusLine());
        return ResponseEntity.status(response.getStatusLine()
                .getStatusCode())
                .headers(responseHeaders)
                .body(outputStream.toString(charsetName));
    }

    private HttpUriRequest buildBackendRequest(String path, HttpServletRequest request) throws IOException {
        RequestBuilder requestBuilder = RequestBuilder.create(request.getMethod());

        requestBuilder.setUri(appProperties.getBackend() + '/' + path)
                .setEntity(new InputStreamEntity(request.getInputStream()));

        Enumeration<String> headers = request.getHeaders(TRACE_HEADER_NAME);
        String traceHeaderValue;
        if (headers == null || !headers.hasMoreElements()) {
            LOGGER.debug("No demo-trace header found in request. Creating a new one.");
            traceHeaderValue = idManager.getSingleBundle().getSpiffeId() + "#";
        } else {
            String oldHeader = headers.nextElement();
            LOGGER.debug("Received demo-trace header with content: {}", oldHeader);
            traceHeaderValue = oldHeader + idManager.getSingleBundle().getSpiffeId() + "#";
        }
        LOGGER.debug("Adding header with name {} and value {} to forwarded request", TRACE_HEADER_NAME, traceHeaderValue);
        requestBuilder.addHeader(TRACE_HEADER_NAME, traceHeaderValue);
        return requestBuilder.build();
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
