package com.shipmonk.testingday.connector.exchangerates.exception;

import lombok.Getter;

/**
 * Exception thrown when there's a client error (4xx) from the external API.
 *
 * @author Radovan Šinko
 */
@Getter
public class SystemApiClientException extends RuntimeException {

    private final BadRequestType badRequestType;

    /**
     * Constructs a new SystemApiClientException with the specified bad request type and message.
     *
     * @param badRequestType the type of bad request
     * @param message        the detail message
     */
    public SystemApiClientException(final BadRequestType badRequestType, final String message) {
        super(message);
        this.badRequestType = badRequestType;
    }

    /**
     * Constructs a new SystemApiClientException with the specified bad request type, message, and cause.
     *
     * @param badRequestType the type of bad request
     * @param message        the detail message
     * @param cause          the cause of the exception
     */
    public SystemApiClientException(final BadRequestType badRequestType, final String message, final Throwable cause) {
        super(message, cause);
        this.badRequestType = badRequestType;
    }

    public enum BadRequestType {
        INVALID_REQUEST,
        INVALID_PARAMETERS,
        RATE_LIMIT_EXCEEDED
    }
}
