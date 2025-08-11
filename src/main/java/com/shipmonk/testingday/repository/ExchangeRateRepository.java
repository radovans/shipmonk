package com.shipmonk.testingday.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shipmonk.testingday.repository.entity.ExchangeRate;

/**
 * Repository for exchange rate operations.
 *
 * @author Radovan Šinko
 */
@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    /**
     * Find all exchange rates for a specific date and base currency.
     *
     * @param date         the date to search for
     * @param baseCurrency the base currency
     * @return list of exchange rates
     */
    List<ExchangeRate> findByDateAndBaseCurrency(LocalDate date, String baseCurrency);

    /**
     * Check if exchange rates exist for a specific date and base currency.
     *
     * @param date         the date to check
     * @param baseCurrency the base currency
     * @return true if rates exist, false otherwise
     */
    boolean existsByDateAndBaseCurrency(LocalDate date, String baseCurrency);
}
