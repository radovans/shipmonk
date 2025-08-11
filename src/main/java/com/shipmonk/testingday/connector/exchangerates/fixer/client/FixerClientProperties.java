package com.shipmonk.testingday.connector.exchangerates.fixer.client;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Fixer client configuration properties.
 *
 * @author Radovan Šinko
 */
@Data
@Validated
@ConfigurationProperties(FixerClientProperties.PREFIX)
public class FixerClientProperties {

    static final String PREFIX = "integration.fixer-client";

    @NotBlank(message = "Fixer API URL cannot be null or empty")
    private String url;

    @NotBlank(message = "Fixer API key cannot be null or empty")
    private String apiKey;
}
