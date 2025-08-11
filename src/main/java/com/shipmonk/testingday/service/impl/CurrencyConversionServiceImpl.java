package com.shipmonk.testingday.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.shipmonk.testingday.facade.ExchangeRatesProperties;
import com.shipmonk.testingday.service.CurrencyConversionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link CurrencyConversionService}
 *
 * @author Radovan Šinko
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyConversionServiceImpl implements CurrencyConversionService {

    private final ExchangeRatesProperties exchangeRatesProperties;

    @Override
    public Map<String, BigDecimal> convertBaseCurrency(
        final Map<String, BigDecimal> originalRates,
        final String sourceBaseCurrency,
        final String targetBaseCurrency) {

        Assert.notNull(originalRates, "Original rates cannot be null");
        Assert.notEmpty(originalRates, "Original rates cannot be empty");
        Assert.hasText(sourceBaseCurrency, "Source base currency cannot be null or empty");
        Assert.hasText(targetBaseCurrency, "Target base currency cannot be null or empty");

        if (sourceBaseCurrency.equals(targetBaseCurrency)) {
            return new HashMap<>(originalRates);
        }

        log.debug("Converting exchange rates from {} to {}", sourceBaseCurrency, targetBaseCurrency);

        final BigDecimal targetCurrencyRate = originalRates.get(targetBaseCurrency);
        if (targetCurrencyRate == null) {
            throw new IllegalArgumentException(
                "Target base currency " + targetBaseCurrency + " not found in exchange rates");
        }

        final Map<String, BigDecimal> convertedRates = new HashMap<>();

        for (Map.Entry<String, BigDecimal> entry : originalRates.entrySet()) {
            final String currency = entry.getKey();
            final BigDecimal rate = entry.getValue();

            if (currency.equals(sourceBaseCurrency)) {
                convertedRates.put(
                    currency, BigDecimal.ONE.divide(targetCurrencyRate, exchangeRatesProperties.getRoundingScale(),
                    exchangeRatesProperties.getRoundingMode()));
            } else if (currency.equals(targetBaseCurrency)) {
                convertedRates.put(currency, BigDecimal.ONE);
            } else {
                convertedRates.put(
                    currency, rate.divide(targetCurrencyRate, exchangeRatesProperties.getRoundingScale(),
                    exchangeRatesProperties.getRoundingMode()));
            }
        }

        log.debug("Successfully converted {} exchange rates", convertedRates.size());
        return convertedRates;
    }

    @Override
    public String getApiBaseCurrency(final String desiredBaseCurrency) {
        Assert.hasText(desiredBaseCurrency, "Desired base currency cannot be null or empty");

        if (isBaseCurrencySupported(desiredBaseCurrency)) {
            return desiredBaseCurrency;
        }

        final String fallbackCurrency = exchangeRatesProperties.getFallbackCurrency();
        log.debug("Desired base currency {} not supported by API, using fallback: {}",
            desiredBaseCurrency, fallbackCurrency);
        return fallbackCurrency;
    }

    @Override
    public boolean isBaseCurrencySupported(final String baseCurrency) {
        Assert.hasText(baseCurrency, "Base currency cannot be null or empty");
        return !exchangeRatesProperties.getUnsupportedApiBaseCurrencies().contains(baseCurrency);
    }
}
