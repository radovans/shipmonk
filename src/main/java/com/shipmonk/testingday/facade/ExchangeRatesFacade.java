package com.shipmonk.testingday.facade;

import java.time.LocalDate;

import com.shipmonk.testingday.api.dto.ExchangeRatesDto;

/**
 * Facade for exchange rates operations.
 * This serves as the external interface for the exchange rates service.
 *
 * @author Radovan Šinko
 */
public interface ExchangeRatesFacade {

    /**
     * Get latest exchange rates.
     * This method will check cache first, and if not found, fetch from external API.
     *
     * @return Latest exchange rates.
     */
    ExchangeRatesDto getLatestRates();

    /**
     * Get exchange rates for a specific date.
     * This method will check cache first, and if not found, fetch from external API.
     *
     * @param date Date to get exchange rates for.
     * @return Exchange rates for the specified date.
     */
    ExchangeRatesDto getRatesForDate(LocalDate date);
}
