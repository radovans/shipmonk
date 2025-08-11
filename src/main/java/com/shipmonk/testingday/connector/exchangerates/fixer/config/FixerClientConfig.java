package com.shipmonk.testingday.connector.exchangerates.fixer.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import com.shipmonk.testingday.connector.exchangerates.fixer.client.FixerClientProperties;

/**
 * Configuration for Fixer client.
 *
 * @author Radovan Šinko
 */
@Configuration
@EnableConfigurationProperties(FixerClientProperties.class)
public class FixerClientConfig {

    /**
     * Creates a RestClient bean for Fixer API.
     *
     * @param properties the Fixer client properties
     * @return the RestClient bean
     */
    @Bean
    @Qualifier("fixerRestClient")
    public RestClient fixerRestClient(final FixerClientProperties properties) {
        return RestClient.builder()
            .baseUrl(properties.getUrl())
            .build();
    }
}
