package com.nurtricenter.apigateway.configuration;

import com.nurtricenter.apigateway.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
public class JwtDecoderConfiguration {

    private final JwtService jwtService;

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        return token -> {
            try {
                if (!jwtService.validateToken(token)) {
                    return Mono.error(new JwtException("Invalid token"));
                }

                var claims = jwtService.extractAllClaims(token);

                Jwt jwt = Jwt.withTokenValue(token)
                        .header("alg", "HS256")
                        .header("typ", "JWT")
                        .subject(claims.getSubject())
                        .claim("roles", claims.get("roles"))
                        .build();

                return Mono.just(jwt);
            } catch (Exception e) {
                return Mono.error(new JwtException("Error decoding token: " + e.getMessage()));
            }
        };
    }

}
