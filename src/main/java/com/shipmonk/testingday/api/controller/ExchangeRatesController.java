package com.shipmonk.testingday.api.controller;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.shipmonk.testingday.api.DateValidator;
import com.shipmonk.testingday.facade.ExchangeRatesFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for handling exchange rates.
 *
 * @author Radovan Šinko
 */
@RestController
@RequestMapping(path = "/api/v1/rates")
@RequiredArgsConstructor
@Slf4j
public class ExchangeRatesController {

    private final ExchangeRatesFacade exchangeRatesFacade;

    private final DateValidator dateValidator;

    /**
     * Endpoint to get exchange rates for a specific day.
     *
     * @param day the date in "yyyy-MM-dd" format
     * @return ResponseEntity with exchange rates for the specified day
     */
    @RequestMapping(method = RequestMethod.GET, path = "/{day}")
    public ResponseEntity<Object> getRates(@PathVariable("day") final String day) {
        log.info("Getting rates for day {}", day);
        final LocalDate date = dateValidator.validateDate(day);

        if (date.isEqual(LocalDate.now())) {
            return ResponseEntity.ok(exchangeRatesFacade.getLatestRates());
        }

        return ResponseEntity.ok(exchangeRatesFacade.getRatesForDate(date));
    }
}
