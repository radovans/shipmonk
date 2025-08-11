package com.shipmonk.testingday.connector.exchangerates.fixer.client.impl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClient;

import com.shipmonk.testingday.connector.exchangerates.exception.SystemApiClientException;
import com.shipmonk.testingday.connector.exchangerates.exception.SystemApiServerException;
import com.shipmonk.testingday.connector.exchangerates.fixer.client.FixerClient;
import com.shipmonk.testingday.connector.exchangerates.fixer.client.FixerClientProperties;
import com.shipmonk.testingday.connector.exchangerates.fixer.dto.ExchangeRatesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link FixerClient}
 *
 * @author Radovan Šinko
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FixerClientImpl implements FixerClient {

    private static final String API_KEY = "access_key";
    private static final String BASE = "base";
    private static final String LATEST = "latest";

    private final RestClient restClient;
    private final FixerClientProperties fixerClientProperties;

    @Override
    public ExchangeRatesResponse getLatestRates(final String base) {
        Assert.hasText(base, "Base currency cannot be null or empty");

        final Map<String, String> queryParams = fillQueryParams(base);
        return callGet("/" + LATEST, queryParams);
    }

    @Override
    public ExchangeRatesResponse getRatesForDate(final String base, final LocalDate date) {
        Assert.hasText(base, "Base currency cannot be null or empty");
        Assert.notNull(date, "Date cannot be null");

        final Map<String, String> queryParams = fillQueryParams(base);
        return callGet("/" + date, queryParams);
    }

    private ExchangeRatesResponse callGet(final String path, final Map<String, String> queryParams) {
        try {
            final ResponseEntity<ExchangeRatesResponse> response = restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path(path);
                    queryParams.forEach(uriBuilder::queryParam);
                    return uriBuilder.build();
                })
                .retrieve()
                .onStatus(
                    HttpStatusCode::is4xxClientError,
                    (request, response1) -> {
                        throw new SystemApiClientException(
                            SystemApiClientException.BadRequestType.INVALID_REQUEST,
                            "Request failed: " + response1.getStatusCode());
                    })
                .onStatus(
                    HttpStatusCode::is5xxServerError,
                    (request, response1) -> {
                        throw new SystemApiServerException(
                            SystemApiServerException.ServerErrorRequestType.SERVICE_UNAVAILABLE,
                            "Remote service error: " + response1.getStatusCode());
                    })
                .toEntity(ExchangeRatesResponse.class);

            return response.getBody();
        } catch (SystemApiClientException | SystemApiServerException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error calling Fixer API: {}", e.getMessage(), e);
            throw new SystemApiServerException(
                SystemApiServerException.ServerErrorRequestType.INTERNAL_SERVER_ERROR,
                "Unexpected error occurred", e);
        }
    }

    private Map<String, String> fillQueryParams(final String base) {
        final Map<String, String> queryParams = new HashMap<>();

        queryParams.put(API_KEY, fixerClientProperties.getApiKey());

        if (!StringUtils.isEmpty(base)) {
            queryParams.put(BASE, base);
        }
        return queryParams;
    }
}
