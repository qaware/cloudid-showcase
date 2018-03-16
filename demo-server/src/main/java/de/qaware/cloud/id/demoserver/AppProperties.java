package de.qaware.cloud.id.demoserver;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotBlank;

/**
 * Application properties.
 */
@Slf4j
@Data
@Validated
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    @NotBlank
    private String backend;

    @NotBlank
    private String clientAuth;

    /**
     * Log the config values on debug.
     */
    @PostConstruct
    public void debugLog() {
        LOGGER.debug("Backend: {}", backend);
        LOGGER.debug("Client Auth: {}", clientAuth);
    }

}
