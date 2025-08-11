package com.shipmonk.testingday.connector.exchangerates.fixer.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import lombok.Data;

/**
 * DTO for exchange rates for single date from Fixer API.
 *
 * @author Radovan Šinko
 */
@Data
public class ExchangeRatesResponse {

    private Boolean success;

    private Boolean historical;

    private LocalDate date;

    private String base;

    private Map<String, BigDecimal> rates;
}
