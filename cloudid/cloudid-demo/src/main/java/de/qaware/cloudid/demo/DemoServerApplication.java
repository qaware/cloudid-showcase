package de.qaware.cloudid.demo;

import de.qaware.cloudid.lib.jsa.CloudIdProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.filters.RequestDumperFilter;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import javax.servlet.Filter;

/**
 * Demo server Spring Boot application.
 */
@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class DemoServerApplication {

    private final AppProperties appProperties;

    /**
     * Application entry point
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        CloudIdProvider.install();

        new SpringApplicationBuilder(DemoServerApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }

    /**
     * Tomcat request dumper filter
     *
     * @return filter
     */
    @Bean
    public FilterRegistrationBean<Filter> filterRegistrationBean() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();

        if (appProperties.isLogRequests()) {
            Filter requestDumperFilter = new RequestDumperFilter();
            registration.setFilter(requestDumperFilter);
            registration.addUrlPatterns("/*");
        }

        return registration;

    }

}
