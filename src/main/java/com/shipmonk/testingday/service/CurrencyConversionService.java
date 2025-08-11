package com.shipmonk.testingday.service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Service for handling currency conversion when the desired base currency
 * is not supported by external APIs.
 *
 * @author Radovan Šinko
 */
public interface CurrencyConversionService {

    /**
     * Convert exchange rates from one base currency to another.
     *
     * @param originalRates      Original exchange rates with source base currency
     * @param sourceBaseCurrency The base currency of the original rates
     * @param targetBaseCurrency The desired base currency
     * @return Converted exchange rates with target base currency
     */
    Map<String, BigDecimal> convertBaseCurrency(
        Map<String, BigDecimal> originalRates,
        String sourceBaseCurrency,
        String targetBaseCurrency
    );

    /**
     * Get the appropriate base currency to use when fetching from external API.
     * This method determines whether to use the desired base currency or fallback
     * based on API support.
     *
     * @param desiredBaseCurrency The base currency we want to achieve
     * @return The base currency to use when calling external API
     */
    String getApiBaseCurrency(String desiredBaseCurrency);

    /**
     * Check if the given base currency is supported by the external API.
     *
     * @param baseCurrency The base currency to check
     * @return true if supported, false otherwise
     */
    boolean isBaseCurrencySupported(String baseCurrency);
}
