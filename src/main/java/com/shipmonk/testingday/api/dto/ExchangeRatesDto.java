package com.shipmonk.testingday.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

/**
 * DTO for exchange rates.
 *
 * @author Radovan Šinko
 */
@Data
@Builder
public class ExchangeRatesDto {

    @NotBlank(message = "Base currency cannot be null or empty")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Base currency must be a 3-letter uppercase currency code")
    private String base;

    @NotNull(message = "Date cannot be null")
    private LocalDate date;

    @NotNull(message = "Rates map cannot be null")
    private Map<String, BigDecimal> rates;
}
