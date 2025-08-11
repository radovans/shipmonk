package com.shipmonk.testingday.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shipmonk.testingday.facade.ExchangeRatesProperties;
import com.shipmonk.testingday.service.impl.CurrencyConversionServiceImpl;

@ExtendWith(MockitoExtension.class)
class CurrencyConversionServiceTest {

    @Mock
    private ExchangeRatesProperties exchangeRatesProperties;

    private CurrencyConversionService currencyConversionService;

    @BeforeEach
    void setUp() {
        lenient().when(exchangeRatesProperties.getFallbackCurrency()).thenReturn("EUR");
        lenient().when(exchangeRatesProperties.getUnsupportedApiBaseCurrencies()).thenReturn(Set.of("USD"));
        lenient().when(exchangeRatesProperties.getRoundingScale()).thenReturn(6);
        lenient().when(exchangeRatesProperties.getRoundingMode()).thenReturn(RoundingMode.HALF_UP);
        currencyConversionService = new CurrencyConversionServiceImpl(exchangeRatesProperties);
    }

    @Test
    void testConvertBaseCurrency_SameBaseCurrency() {
        // Given: EUR-based rates
        Map<String, BigDecimal> rates = new HashMap<>();
        rates.put("EUR", BigDecimal.ONE);
        rates.put("USD", new BigDecimal("1.0850"));

        // When: Convert EUR to EUR (no conversion needed)
        Map<String, BigDecimal> result = currencyConversionService.convertBaseCurrency(rates, "EUR", "EUR");

        // Then: Should return the same rates
        assertEquals(rates, result);
    }

    @Test
    void testConvertBaseCurrency_TargetCurrencyNotFound() {
        // Given: EUR-based rates without USD
        Map<String, BigDecimal> rates = new HashMap<>();
        rates.put("EUR", BigDecimal.ONE);
        rates.put("GBP", new BigDecimal("0.8600"));

        // When & Then: Should throw exception when trying to convert to USD
        assertThrows(IllegalArgumentException.class, () -> {
            currencyConversionService.convertBaseCurrency(rates, "EUR", "USD");
        });
    }

    @Test
    void testIsBaseCurrencySupported() {
        // Test supported currencies
        assertTrue(currencyConversionService.isBaseCurrencySupported("EUR"));
        assertTrue(currencyConversionService.isBaseCurrencySupported("GBP"));
        assertTrue(currencyConversionService.isBaseCurrencySupported("JPY"));

        // Test unsupported currencies
        assertFalse(currencyConversionService.isBaseCurrencySupported("USD"));
    }

    @Test
    void testGetApiBaseCurrency() {
        // When: Requesting supported currency
        String result1 = currencyConversionService.getApiBaseCurrency("EUR");
        assertEquals("EUR", result1);

        // When: Requesting unsupported currency
        String result2 = currencyConversionService.getApiBaseCurrency("USD");
        assertEquals("EUR", result2);
    }

    @Test
    void testBidirectionalConversion_USD_to_EUR_and_back() {
        // Given: USD-based rates where 1 USD = 2 EUR
        Map<String, BigDecimal> usdBasedRates = new HashMap<>();
        usdBasedRates.put("USD", BigDecimal.ONE);
        usdBasedRates.put("EUR", new BigDecimal("2.0000"));
        usdBasedRates.put("GBP", new BigDecimal("1.5000"));

        // When: Convert from USD to EUR base
        Map<String, BigDecimal> eurBasedRates = currencyConversionService.convertBaseCurrency(
            usdBasedRates, "USD", "EUR");

        // Then: EUR should be the base currency (rate = 1.0)
        assertEquals(BigDecimal.ONE, eurBasedRates.get("EUR"));

                // And: USD rate should be 0.5 (1/2) - meaning 1 EUR = 0.5 USD
        assertEquals(new BigDecimal("0.500000"), eurBasedRates.get("USD"));

        // And: GBP rate should be 0.75 (1.5/2) - meaning 1 EUR = 0.75 GBP
        assertEquals(new BigDecimal("0.750000"), eurBasedRates.get("GBP"));

        // When: Convert back from EUR to USD base
        Map<String, BigDecimal> backToUsdRates = currencyConversionService.convertBaseCurrency(
            eurBasedRates, "EUR", "USD");

        // Then: Should get back to the original rates (within rounding precision)
        assertEquals(BigDecimal.ONE, backToUsdRates.get("USD"));
        assertEquals(new BigDecimal("2.000000"), backToUsdRates.get("EUR"));
        assertEquals(new BigDecimal("1.500000"), backToUsdRates.get("GBP"));
    }

    @Test
    void testBidirectionalConversion_EUR_to_USD_and_back() {
        // Given: EUR-based rates where 1 EUR = 0.5 USD
        Map<String, BigDecimal> eurBasedRates = new HashMap<>();
        eurBasedRates.put("EUR", BigDecimal.ONE);
        eurBasedRates.put("USD", new BigDecimal("0.5000"));
        eurBasedRates.put("GBP", new BigDecimal("0.7500"));

        // When: Convert from EUR to USD base
        Map<String, BigDecimal> usdBasedRates = currencyConversionService.convertBaseCurrency(
            eurBasedRates, "EUR", "USD");

        // Then: USD should be the base currency (rate = 1.0)
        assertEquals(BigDecimal.ONE, usdBasedRates.get("USD"));

        // And: EUR rate should be 2.0 (1/0.5) - meaning 1 USD = 2 EUR
        assertEquals(new BigDecimal("2.000000"), usdBasedRates.get("EUR"));
        // And: GBP rate should be 1.5 (0.75/0.5) - meaning 1 USD = 1.5 GBP
        assertEquals(new BigDecimal("1.500000"), usdBasedRates.get("GBP"));

        // When: Convert back from USD to EUR base
        Map<String, BigDecimal> backToEurRates = currencyConversionService.convertBaseCurrency(
            usdBasedRates, "USD", "EUR");

        // Then: Should get back to the original rates (within rounding precision)
        assertEquals(BigDecimal.ONE, backToEurRates.get("EUR"));
        assertEquals(new BigDecimal("0.5000"), backToEurRates.get("USD"));
        assertEquals(new BigDecimal("0.7500"), backToEurRates.get("GBP"));
    }
}
