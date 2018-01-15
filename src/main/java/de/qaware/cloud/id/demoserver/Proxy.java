package de.qaware.cloud.id.demoserver;

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
@Controller
public class Proxy {

    @Value("${de.qaware.cloud.id.demoserver.backend}")
    private String backend;

    @Autowired
    private HttpClient httpClient;

    @RequestMapping("/proxy/{path}")
    public ResponseEntity forwardRequest(@PathVariable String path, HttpServletRequest request) throws IOException {
        HttpUriRequest backendRequest = buildBackendRequest(path, request);
        HttpResponse response = httpClient.execute(backendRequest);

        HttpHeaders responseHeaders = convertResponseHeaders(response);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        response.getEntity().writeTo(outputStream);
        String charsetName = responseHeaders.getContentType() != null ? responseHeaders.getContentType().getCharset().name() : "UTF-8";
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
