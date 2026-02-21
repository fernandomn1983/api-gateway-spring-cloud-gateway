package com.nurtricenter.apigateway.configuration.security;

import com.nurtricenter.apigateway._shared.constant.CommonConstant;
import com.nurtricenter.apigateway.security.value.SecurityValue;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

import static com.nurtricenter.apigateway.configuration.security.SecurityConstant.*;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final SecurityValue securityValue;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager(
            ReactiveUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder);

        return authenticationManager;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(securityValue.getPublicList())
                        .permitAll()
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {
                }))
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(securityValue.getCorsAllowedOrigins());
        corsConfiguration.setAllowedMethods(List.of(GET_METHOD, POST_METHOD, PUT_METHOD, DELETE_METHOD, OPTIONS_METHOD));
        corsConfiguration.setAllowedHeaders(List.of(ANY_HEADER));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setExposedHeaders(List.of(
                CommonConstant.X_REQUEST_ID_HEADER,
                CommonConstant.X_RATE_LIMIT_LIMIT_HEADER,
                CommonConstant.X_RATE_LIMIT_REMAINING_HEADER,
                AUTHORIZATION_HEADER
        ));
        corsConfiguration.setMaxAge(ONE_HOUR);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(ANY_PATH_WILDCARD, corsConfiguration);

        return source;
    }

}
