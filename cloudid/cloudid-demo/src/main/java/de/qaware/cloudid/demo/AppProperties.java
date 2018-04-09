package de.qaware.cloudid.demo;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;

/**
 * Application properties.
 */
@Slf4j
@Data
@Validated
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    /**
     * Whether to log actual requests.
     */
    private boolean logRequests;

    /**
     * Log the config values on debug.
     */
    @PostConstruct
    public void debugLog() {
        LOGGER.debug("App Properties: {}", this);
    }

}
