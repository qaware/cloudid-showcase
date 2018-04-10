package de.qaware.cloudid.demo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
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

    private final AppProperties appProperties;

    /**
     *
     *
     * @param path    request path
     * @param request request
     * @return response entity
     */
    @RequestMapping("/{path}")
    public ResponseEntity demoPageRequest(@PathVariable String path, HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaders("X-demo-trace");
        String trace;
        if (headers.hasMoreElements()) {
            trace = headers.nextElement();
            trace = trace.replace("#", "<br />");
        } else {
            trace = "NO TRACE AVAILABLE!";
        }
        return ResponseEntity.status(200)
                .body("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \n" +
                        "\"http://www.w3.org/TR/html4/loose.dtd\">\n" +
                        "<html>\n" +
                        "<head>\n" +
                        "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
                        "<title>CNI Demo</title>\n" +
                        "</head>\n" +
                        "<body>Call trace:<br />" +
                        trace +
                        "</body>\n" +
                        "</html>");
    }
}
