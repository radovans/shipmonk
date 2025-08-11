package com.shipmonk.testingday.api;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Class representing an API error response.
 *
 * @author Radovan Šinko
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ApiError {

    @NonNull
    private List<String> errors;

    private LocalDateTime timestamp = LocalDateTime.now();
}
