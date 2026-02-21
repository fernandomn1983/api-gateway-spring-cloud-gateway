package com.nurtricenter.apigateway.filter.ratelimiting;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RateLimitingConstant {

    public static final int MAX_REQUESTS_PER_MINUTE = 60;
    public static final String RATE_LIMIT_KEY = "rate_limit";
    public static final int FIRST_PLACE = 1;
    public static final int ONE = 1;

}
