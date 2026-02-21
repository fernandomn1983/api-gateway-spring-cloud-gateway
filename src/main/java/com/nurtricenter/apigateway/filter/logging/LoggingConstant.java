package com.nurtricenter.apigateway.filter.logging;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LoggingConstant {

    public static final String REQUEST_DETAILS_FORMAT_LOG = """
            Request Details:
            requestId = {}
            method = {}
            path = {}
            headers = {}
            clientIp = {}
            """;
    public static final String ERROR_PROCESSING_REQUEST_LOG = "Error processing request: {}";
    public static final String REQUEST_FAILED_LOG = "Request failed: {}";
    public static final String RESPONSE_DETAILS_FORMAT_LOG = """
            Response Details:
            requestId = {}
            statusCode = {}
            duration = {} ms
            """;
    public static final String X_RESPONSE_TIME_HEADER = "X-Response-Time";
    public static final String MILLISECONDS = "ms";
    public static final String CANNOT_MODIFY_RESPONSE_HEADERS_RESPONSE_ALREADY_COMMITTED_LOG = "Cannot modify response headers - response already committed.";
    public static final int SECOND_PLACE = 2;

}
