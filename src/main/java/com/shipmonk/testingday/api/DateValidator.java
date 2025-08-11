package com.shipmonk.testingday.api;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.stereotype.Component;

/**
 * Validator for date format validation.
 *
 * @author Radovan Šinko
 */
@Component
public class DateValidator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Validates if the given date string is in the correct format and not in the future.
     *
     * @param dateString the date string to validate
     * @return the parsed LocalDate if valid
     * @throws IllegalArgumentException if the date is invalid or in the future
     */
    public LocalDate validateDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            throw new IllegalArgumentException("Date parameter cannot be null or empty");
        }

        try {
            final LocalDate date = LocalDate.parse(dateString, DATE_FORMATTER);

            if (date.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Cannot request rates for future dates");
            }

            return date;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. " +
                "Please provide a valid date in yyyy-MM-dd format.");
        }
    }
}
