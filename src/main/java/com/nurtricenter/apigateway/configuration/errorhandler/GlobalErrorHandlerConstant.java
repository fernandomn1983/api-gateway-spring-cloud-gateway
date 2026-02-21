package com.nurtricenter.apigateway.configuration.errorhandler;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GlobalErrorHandlerConstant {

    public static final int GLOBAL_ERROR_HANDLER_ORDER = -1;
    public static final String NOT_FOUND_ERROR = "Not Found";
    public static final String REQUESTED_RESOURCE_NOT_FOUND_MSG = "The requested resource was not found.";
    public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
    public static final String UNEXPECTED_ERROR_OCCURRED_MSG = "An unexpected error occurred.";
    public static final String ERROR_MESSAGE_FORMAT = """
            {
                "error": "%s",
                "message": "%s",
                "timestamp": "%s"
            }
            """;

}
