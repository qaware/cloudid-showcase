package de.qaware.cloudid.demo;

import de.qaware.cloudid.lib.CloudId;
import de.qaware.cloudid.lib.IdManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import static de.qaware.cloudid.demo.util.TraceFormatter.getFormattedTrace;

/**
 * Simple proxy controller. Performs some requests against a configurable backend.
 */
@Slf4j
@Controller
@ConfigurationProperties
@RequiredArgsConstructor
public class DemoResponder {

    private static final String RESPONSE_TEMPLATE_NAME = "demo";
    private final IdManager idManager = CloudId.getIdManager();

    /**
     * Generates a html demo page which contains the SPIFFE callstack, if available.
     *
     * @param request request
     * @param model   model supplied by and for thymeleaf
     * @return response entity with the generated html page
     */
    @RequestMapping("/")
    public String demoPageRequest(HttpServletRequest request, Model model) {
        model
                .addAttribute("myid", idManager.getWorkloadId().getSpiffeId())
                .addAttribute("trace", getFormattedTrace(request));
        return RESPONSE_TEMPLATE_NAME;
    }
}
