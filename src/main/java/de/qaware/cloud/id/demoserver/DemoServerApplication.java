package de.qaware.cloud.id.demoserver;

import de.qaware.cloud.id.spire.SPIREProvider;
import org.apache.catalina.filters.RequestDumperFilter;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import javax.servlet.Filter;

/**
 * Demo server Spring Boot application.
 */
@SpringBootApplication
public class DemoServerApplication {

    /**
     * Application entry point
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        new SPIREProvider().installAsDefault();

        SpringApplication.run(DemoServerApplication.class, args);
    }

    /**
     * Get an application-global HTTP client
     *
     * @return HTTP client
     */
    @Bean
    public HttpClient getHttpClient() {
        return HttpClientBuilder.create().build();
    }

    /**
     * Tomcat request dumper filter
     *
     * @return filter
     */
    @Bean
    public FilterRegistrationBean requestDumperFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        Filter requestDumperFilter = new RequestDumperFilter();
        registration.setFilter(requestDumperFilter);
        registration.addUrlPatterns("/proxy/*");
        return registration;
    }

}
