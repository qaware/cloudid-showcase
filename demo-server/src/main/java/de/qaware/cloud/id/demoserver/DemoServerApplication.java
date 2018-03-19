package de.qaware.cloud.id.demoserver;

import de.qaware.cloud.id.spire.jsa.SPIREContextFactory;
import de.qaware.cloud.id.spire.jsa.SPIREProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.filters.RequestDumperFilter;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClients;
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
        SPIREProvider.install();

        new SpringApplicationBuilder(DemoServerApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }

    /**
     * Get an application-global HTTP client
     *
     * @return HTTP client
     */
    @Bean
    public HttpClient getHttpClient() {
        return HttpClients.custom()
                .setSSLContext(SPIREContextFactory.get())
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .build();

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
            registration.addUrlPatterns("/proxy/*");
        }

        return registration;

    }

}
