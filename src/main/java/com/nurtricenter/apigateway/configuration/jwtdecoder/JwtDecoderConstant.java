package com.nurtricenter.apigateway.configuration.jwtdecoder;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtDecoderConstant {

    public static final String INVALID_TOKEN = "Invalid token";
    public static final String ALG_KEY = "alg";
    public static final String TYP_KEY = "typ";
    public static final String HS256_ALG = "HS256";
    public static final String JWT_TYP = "JWT";
    public static final String ERROR_DECODING_TOKEN_FORMAT = "Error decoding token: %s";

}
