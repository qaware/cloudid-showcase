package de.qaware.cloud.id.demoserver;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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
}
