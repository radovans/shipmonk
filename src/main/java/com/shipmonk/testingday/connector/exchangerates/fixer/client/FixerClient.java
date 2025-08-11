package com.shipmonk.testingday.connector.exchangerates.fixer.client;

import java.time.LocalDate;

import com.shipmonk.testingday.connector.exchangerates.fixer.dto.ExchangeRatesResponse;

/**
 * Fixer client.
 *
 * @author Radovan Šinko
 */
public interface FixerClient {

    /**
     * Get latest exchange rates.
     *
     * @param base Base currency code (optional).
     * @return Latest exchange rates.
     */
    ExchangeRatesResponse getLatestRates(String base);

    /**
     * Get historical exchange rates.
     *
     * @param date Date to get exchange rates for.
     * @param base Base currency code (optional).
     * @return Historical exchange rates.
     */
    ExchangeRatesResponse getRatesForDate(String base, LocalDate date);
}
