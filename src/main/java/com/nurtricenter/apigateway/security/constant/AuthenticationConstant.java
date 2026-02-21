package com.nurtricenter.apigateway.security.constant;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthenticationConstant {

    public static final String AUTHENTICATION_ATTEMPT_USER_LOG = "Authentication attempt for user: {}";
    public static final String AUTHENTICATION_SUCCESSFUL_USER_LOG = "Authentication successful for user: {}";
    public static final String USERNAME_KEY = "username";
    public static final String BEARER_TYPE = "Bearer";
    public static final String AUTHENTICATION_FAILED_USER_ERROR_LOG = "Authentication failed for user: {}, error: {}";
    public static final String INVALID_USERNAME_PASSWORD_MSG = "Invalid username and/or password";

}
