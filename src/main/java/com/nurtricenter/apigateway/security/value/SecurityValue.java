package com.nurtricenter.apigateway.security.value;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "security")
@Getter
@Setter
public class SecurityValue {

    private List<String> corsAllowedOrigins;
    private String[] publicList;

}
