package com.nurtricenter.apigateway.configuration.security;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityConstant {

    public static final String GET_METHOD = "GET";
    public static final String POST_METHOD = "POST";
    public static final String PUT_METHOD = "PUT";
    public static final String DELETE_METHOD = "DELETE";
    public static final String OPTIONS_METHOD = "OPTIONS";
    public static final String ANY_HEADER = "*";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String ANY_PATH_WILDCARD = "/**";
    public static final long ONE_HOUR = 3600L;

}
