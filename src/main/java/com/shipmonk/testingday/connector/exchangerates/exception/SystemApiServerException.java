package com.shipmonk.testingday.connector.exchangerates.exception;

import lombok.Getter;

/**
 * Exception thrown when there's a server error (5xx) from the external API.
 *
 * @author Radovan Šinko
 */
@Getter
public class SystemApiServerException extends RuntimeException {

    private final ServerErrorRequestType serverErrorRequestType;

    /**
     * Constructs a new SystemApiServerException with the specified server error request type and message.
     *
     * @param serverErrorRequestType the type of server error
     * @param message                the detail message
     */
    public SystemApiServerException(final ServerErrorRequestType serverErrorRequestType, final String message) {
        super(message);
        this.serverErrorRequestType = serverErrorRequestType;
    }

    /**
     * Constructs a new SystemApiServerException with the specified server error request type, message, and cause.
     *
     * @param serverErrorRequestType the type of server error
     * @param message                the detail message
     * @param cause                  the cause of the exception
     */
    public SystemApiServerException(
        final ServerErrorRequestType serverErrorRequestType,
        final String message,
        final Throwable cause) {

        super(message, cause);
        this.serverErrorRequestType = serverErrorRequestType;
    }

    public enum ServerErrorRequestType {
        SERVICE_UNAVAILABLE,
        INTERNAL_SERVER_ERROR,
        GATEWAY_TIMEOUT
    }
}
