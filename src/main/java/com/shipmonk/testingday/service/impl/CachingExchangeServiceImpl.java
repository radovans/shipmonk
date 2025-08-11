package com.shipmonk.testingday.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.shipmonk.testingday.facade.ExchangeRatesProperties;
import com.shipmonk.testingday.repository.ExchangeRateRepository;
import com.shipmonk.testingday.repository.entity.ExchangeRate;
import com.shipmonk.testingday.service.CachingExchangeService;
import com.shipmonk.testingday.service.CurrencyConversionService;
import com.shipmonk.testingday.service.ExchangeService;
import com.shipmonk.testingday.api.dto.ExchangeRatesDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link CachingExchangeService} with caching logic.
 *
 * @author Radovan Šinko
 */
@EnableConfigurationProperties(ExchangeRatesProperties.class)
@Service
@RequiredArgsConstructor
@Slf4j
public class CachingExchangeServiceImpl implements CachingExchangeService {

    private final ExchangeRateRepository exchangeRateRepository;

    private final ExchangeService fixerExchangeService;

    private final ExchangeRatesProperties exchangeRatesProperties;

    private final CurrencyConversionService currencyConversionService;

    @Override
    public ExchangeRatesDto getLatestRates(final String base) {
        Assert.hasText(base, "Base currency cannot be null or empty");
        return fixerExchangeService.getLatestRates(base);
    }

    @Override
    @Transactional
    public ExchangeRatesDto getRatesForDate(final String requestedBase, final LocalDate date) {
        Assert.hasText(requestedBase, "Requested base currency cannot be null or empty");
        Assert.notNull(date, "Date cannot be null");

        if (exchangeRateRepository.existsByDateAndBaseCurrency(date, requestedBase)) {
            log.debug("Found cached rates for configured base currency: {} on date: {}", requestedBase, date);
            return getRatesFromCache(requestedBase, date);
        }

        log.info("No cached rates found for configured base currency: {} on date: {}, fetching from external API",
            requestedBase, date);
        return fetchAndCacheRatesForDate(requestedBase, date);
    }

    private ExchangeRatesDto fetchAndCacheRatesForDate(final String requestedBase, final LocalDate date) {
        final ExchangeRatesDto rates =
            fixerExchangeService.getRatesForDate(exchangeRatesProperties.getBaseCurrency(), date);
        cacheExchangeRates(rates);

        if (!requestedBase.equals(exchangeRatesProperties.getBaseCurrency())) {
            return convertRatesForRequestedBase(rates, requestedBase);
        }

        return rates;
    }

    private ExchangeRatesDto getRatesFromCache(final String requestedBase, final LocalDate date) {
        final List<ExchangeRate> cachedRates =
            exchangeRateRepository.findByDateAndBaseCurrency(date, exchangeRatesProperties.getBaseCurrency());

        final Map<String, BigDecimal> ratesMap = cachedRates.stream()
            .collect(Collectors.toMap(
                ExchangeRate::getTargetCurrency,
                ExchangeRate::getRate
            ));

        final ExchangeRatesDto cachedRatesDto = ExchangeRatesDto.builder()
            .base(exchangeRatesProperties.getBaseCurrency())
            .date(date)
            .rates(ratesMap)
            .build();

        if (!requestedBase.equals(exchangeRatesProperties.getBaseCurrency())) {
            return convertRatesForRequestedBase(cachedRatesDto, requestedBase);
        }

        return cachedRatesDto;
    }

    private ExchangeRatesDto convertRatesForRequestedBase(ExchangeRatesDto cachedRates, String requestedBase) {
        final Map<String, BigDecimal> convertedRates = currencyConversionService.convertBaseCurrency(
            cachedRates.getRates(),
            cachedRates.getBase(),
            requestedBase
        );

        return ExchangeRatesDto.builder()
            .base(requestedBase)
            .date(cachedRates.getDate())
            .rates(convertedRates)
            .build();
    }

    private void cacheExchangeRates(final ExchangeRatesDto rates) {
        final List<ExchangeRate> exchangeRates = rates.getRates().entrySet().stream()
            .map(entry -> ExchangeRate.builder()
                .date(rates.getDate())
                .baseCurrency(rates.getBase())
                .targetCurrency(entry.getKey())
                .rate(entry.getValue())
                .build())
            .collect(Collectors.toList());

        exchangeRateRepository.saveAll(exchangeRates);
        log.debug("Cached {} exchange rates for base currency: {} on date: {}",
            exchangeRates.size(), rates.getBase(), rates.getDate());
    }
}
