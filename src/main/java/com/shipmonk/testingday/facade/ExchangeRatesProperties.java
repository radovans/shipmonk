package com.shipmonk.testingday.facade;

import java.math.RoundingMode;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * Fixer client configuration properties.
 *
 * @author Radovan Šinko
 */
@Data
@Validated
@ConfigurationProperties(ExchangeRatesProperties.PREFIX)
public class ExchangeRatesProperties {

    static final String PREFIX = "exchange-rates";

    @NotBlank(message = "Base currency cannot be null or empty")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Base currency must be a 3-letter uppercase currency code")
    private String baseCurrency;

    @NotBlank(message = "Fallback currency cannot be null or empty")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Fallback currency must be a 3-letter uppercase currency code")
    private String fallbackCurrency;

    private Set<String> unsupportedApiBaseCurrencies;

    @Positive(message = "Rounding scale must be positive")
    private int roundingScale;

    @NotNull(message = "Rounding mode cannot be null")
    private RoundingMode roundingMode;
}
