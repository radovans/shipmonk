package com.shipmonk.testingday.service;

import java.time.LocalDate;

import com.shipmonk.testingday.api.dto.ExchangeRatesDto;

/**
 * Service for caching exchange rates with business logic.
 *
 * @author Radovan Šinko
 */
public interface CachingExchangeService {

    /**
     * Get latest exchange rates.
     *
     * @param base Base currency code.
     * @return Latest exchange rates.
     */
    ExchangeRatesDto getLatestRates(String base);

    /**
     * Get exchange rates for given date with caching.
     * If rates are not found in cache, fetch from external API and store.
     *
     * @param base Base currency code.
     * @param date Date to get exchange rates for.
     * @return Exchange rates for the specified date.
     */
    ExchangeRatesDto getRatesForDate(String base, LocalDate date);
}
