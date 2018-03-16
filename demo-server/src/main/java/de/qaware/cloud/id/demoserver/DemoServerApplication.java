package de.qaware.cloud.id.demoserver;

import de.qaware.cloud.id.spire.jsa.SPIREProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.filters.RequestDumperFilter;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import javax.servlet.Filter;

/**
 * Demo server Spring Boot application.
 */
@Slf4j
@SpringBootApplication
@ConfigurationProperties
@RequiredArgsConstructor
public class DemoServerApplication {

    private static final int HTTPS_PORT = 8443;
    private final AppProperties appProperties;

    /**
     * Application entry point
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        new SPIREProvider().install();

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
        /*
        HttpClientBuilder builder;
        try {
            builder = HttpClientBuilder.create();
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(new SPIREKeyManagerFactory().engineGetKeyManagers(), new SPIRETrustManagerFactory().engineGetTrustManagers(), SecureRandom.getInstanceStrong());
            SSLConnectionSocketFactory connectionSocketFactory = new SSLConnectionSocketFactory(sslContext);
            builder.setSSLSocketFactory(connectionSocketFactory);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
            throw new RuntimeException("HTTP client refuses to work", e);
        }
        return builder.build();
        */
        return HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
    }

    /**
     * Tomcat request dumper filter
     *
     * @return filter
     */
    @Bean
    public FilterRegistrationBean<Filter> requestDumperFilter() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        Filter requestDumperFilter = new RequestDumperFilter();
        registration.setFilter(requestDumperFilter);
        registration.addUrlPatterns("/proxy/*");
        return registration;
    }

    /**
     * Web server factory customizer for embedded Tomcat.
     *
     * @return web server factory customizer
     */
    @Bean
    public WebServerFactoryCustomizer<ConfigurableTomcatWebServerFactory> webServerFactoryCustomizer() {
        if (Boolean.getBoolean("test.http")) {
            LOGGER.warn("Starting HTTP connector");
            return f -> {
            };
        }

        LOGGER.info("Starting HTTPs connector");
        return factory -> factory.addConnectorCustomizers((TomcatConnectorCustomizer) connector -> {
            connector.setPort(HTTPS_PORT);
            connector.setSecure(true);
            connector.setScheme("https");

            Http11NioProtocol proto = (Http11NioProtocol) connector.getProtocolHandler();
            proto.setSSLEnabled(true);
            proto.setClientAuth(appProperties.getClientAuth());

            proto.setKeystoreFile("");
            proto.setKeystorePass("");
            // TODO: Review
            //proto.setKeystoreType("SunX509");
            proto.setKeystoreType("SPIRE");
            //proto.setTruststoreFile("");
            //proto.setTruststorePass("");
            //proto.setTruststoreType("SPIRE");
        });
    }

}
