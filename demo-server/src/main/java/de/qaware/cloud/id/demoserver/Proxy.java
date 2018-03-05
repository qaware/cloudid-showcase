package de.qaware.cloud.id.demoserver;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.InputStreamEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;

/**
 * Simple proxy controller. Performs some requests against a configurable backend.
 */
@Slf4j
@Controller
public class Proxy {

    @Value("${de.qaware.cloud.id.demoserver.backend}")
    private String backend;

    @Autowired
    private HttpClient httpClient;

    /**
     * Forwards a request 1:1 to the target defined in {@code de.qaware.cloud.id.demoserver.backend}.
     *
     * @param path    request path
     * @param request request
     * @return response entity
     * @throws IOException if a transmission error occurs
     */
    @RequestMapping("/proxy/{path}")
    public ResponseEntity forwardRequest(@PathVariable String path, HttpServletRequest request) throws IOException {
        HttpUriRequest backendRequest = buildBackendRequest(path, request);
        LOGGER.info("Proxy request {} to {}", path, backendRequest);
        HttpResponse response = httpClient.execute(backendRequest);

        HttpHeaders responseHeaders = convertResponseHeaders(response);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        response.getEntity().writeTo(outputStream);
        String charsetName = responseHeaders.getContentType() != null ? responseHeaders.getContentType().getCharset().name() : "UTF-8";
        LOGGER.info("Request was successfully: {}", response.getStatusLine());
        return ResponseEntity.status(response.getStatusLine()
                .getStatusCode())
                .headers(responseHeaders)
                .body(outputStream.toString(charsetName));
    }

    private HttpUriRequest buildBackendRequest(String path, HttpServletRequest request) throws IOException {
        RequestBuilder requestBuilder = RequestBuilder.create(request.getMethod());

        for (String name : Collections.list(request.getHeaderNames())) {
            if (name.equalsIgnoreCase("host")) {
                continue;
            }
            for (String value : Collections.list(request.getHeaders(name))) {
                requestBuilder.addHeader(name, value);
            }
        }

        requestBuilder.setUri(backend + path + '?' + request.getQueryString())
                .setEntity(new InputStreamEntity(request.getInputStream()));

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
