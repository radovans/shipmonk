package com.shipmonk.testingday.facade.impl;

import java.time.LocalDate;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.shipmonk.testingday.facade.ExchangeRatesFacade;
import com.shipmonk.testingday.facade.ExchangeRatesProperties;
import com.shipmonk.testingday.service.CachingExchangeService;
import com.shipmonk.testingday.api.dto.ExchangeRatesDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link ExchangeRatesFacade}.
 * This facade delegates to the caching service and provides a clean external interface.
 *
 * @author Radovan Šinko
 */
@Component
@EnableConfigurationProperties(ExchangeRatesProperties.class)
@RequiredArgsConstructor
@Slf4j
public class ExchangeRatesFacadeImpl implements ExchangeRatesFacade {

    private final CachingExchangeService cachingExchangeService;

    private final ExchangeRatesProperties exchangeRatesProperties;

    @Override
    public ExchangeRatesDto getLatestRates() {
        try {
            return cachingExchangeService.getLatestRates(exchangeRatesProperties.getBaseCurrency());
        } catch (Exception e) {
            log.error("Facade: Error getting latest rates for base currency: {}",
                exchangeRatesProperties.getBaseCurrency(), e);
            throw e;
        }
    }

    @Override
    public ExchangeRatesDto getRatesForDate(final LocalDate date) {
        Assert.notNull(date, "Date cannot be null");

        try {
            return cachingExchangeService.getRatesForDate(exchangeRatesProperties.getBaseCurrency(), date);
        } catch (Exception e) {
            log.error("Facade: Error getting rates for base currency: {} on date: {}",
                exchangeRatesProperties.getBaseCurrency(), date, e);
            throw e;
        }
    }
}
