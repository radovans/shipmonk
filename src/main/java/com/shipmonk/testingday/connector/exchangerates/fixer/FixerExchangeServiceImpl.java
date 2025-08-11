package com.shipmonk.testingday.connector.exchangerates.fixer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import com.shipmonk.testingday.api.dto.ExchangeRatesDto;
import com.shipmonk.testingday.connector.exchangerates.fixer.client.FixerClient;
import com.shipmonk.testingday.connector.exchangerates.fixer.dto.ExchangeRatesResponse;
import com.shipmonk.testingday.facade.ExchangeRatesProperties;
import com.shipmonk.testingday.service.CurrencyConversionService;
import com.shipmonk.testingday.service.ExchangeService;
import lombok.RequiredArgsConstructor;

/**
 * Implementation of {@link ExchangeService}
 *
 * @author Radovan Šinko
 */
@EnableConfigurationProperties(ExchangeRatesProperties.class)
@Service("fixerExchangeService")
@RequiredArgsConstructor
public class FixerExchangeServiceImpl implements ExchangeService {

    private final FixerClient fixerClient;

    private final CurrencyConversionService currencyConversionService;

    @Override
    public ExchangeRatesDto getLatestRates(final String base) {
        final String apiBaseCurrency = currencyConversionService.getApiBaseCurrency(base);
        final ExchangeRatesResponse latestRates = fixerClient.getLatestRates(apiBaseCurrency);
        return getExchangeRatesDto(base, latestRates, apiBaseCurrency);
    }

    @Override
    public ExchangeRatesDto getRatesForDate(final String base, final LocalDate date) {
        final String apiBaseCurrency = currencyConversionService.getApiBaseCurrency(base);
        final ExchangeRatesResponse historicalRates = fixerClient.getRatesForDate(apiBaseCurrency, date);
        return getExchangeRatesDto(base, historicalRates, apiBaseCurrency);
    }

    private ExchangeRatesDto getExchangeRatesDto(
        final String desiredBase,
        final ExchangeRatesResponse apiResponse,
        final String apiBaseCurrency) {

        if (desiredBase.equals(apiBaseCurrency)) {
            return ExchangeRatesDto.builder()
                .base(apiResponse.getBase())
                .date(apiResponse.getDate())
                .rates(apiResponse.getRates())
                .build();
        } else {
            final Map<String, BigDecimal> convertedRates = currencyConversionService.convertBaseCurrency(
                apiResponse.getRates(),
                apiBaseCurrency,
                desiredBase
            );

            return ExchangeRatesDto.builder()
                .base(desiredBase)
                .date(apiResponse.getDate())
                .rates(convertedRates)
                .build();
        }
    }
}
