package com.nurtricenter.apigateway.security.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class AuthResponseDto {

    private String token;
    private String type;
    private Long expiresIn;
    private Map<String, Object> userInfo;
    private String error;

}
