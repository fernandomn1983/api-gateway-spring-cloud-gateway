package com.nurtricenter.apigateway.filter.ratelimiting;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RateLimitingConstant {

	public static final int MAX_REQUESTS_PER_MINUTE = 1000;
	public static final String RATE_LIMIT_KEY = "rate_limit";
	public static final int FIRST_PLACE = 1;

	public static final String RATE_LIMITING_KEY_LOG = "Rate limiting for key: {}";
	public static final String INCREMENTED_COUNT_KEY_LOG = "Incremented count for key {}: {}";
	public static final String FINAL_COUNT_KEY_LOG = "Final count for key {}: {}";
	public static final String RATE_LIMIT_EXCEEDED_KEY_LOG = "Rate limit exceeded for key {}: count {} > {}";
	public static final String ALLOWING_REQUEST_KEY_LOG = "Allowing request for key {}: count {}";
	public static final String ERROR_RATE_LIMITING_KEY_LOG = "Error in rate limiting for key {}: {}";

}
