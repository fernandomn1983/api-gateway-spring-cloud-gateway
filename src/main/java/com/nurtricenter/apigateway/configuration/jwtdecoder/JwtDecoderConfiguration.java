package com.nurtricenter.apigateway.configuration.jwtdecoder;

import com.nurtricenter.apigateway._shared.constant.CommonConstant;
import com.nurtricenter.apigateway.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import reactor.core.publisher.Mono;

import static com.nurtricenter.apigateway.configuration.jwtdecoder.JwtDecoderConstant.*;

@Configuration
@RequiredArgsConstructor
public class JwtDecoderConfiguration {

    private final JwtService jwtService;

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        return token -> {
            try {
                if (!jwtService.validateToken(token)) {
                    return Mono.error(new JwtException(INVALID_TOKEN));
                }

                var claims = jwtService.extractAllClaims(token);

                Jwt jwt = Jwt.withTokenValue(token)
                        .header(ALG_KEY, HS256_ALG)
                        .header(TYP_KEY, JWT_TYP)
                        .subject(claims.getSubject())
                        .claim(CommonConstant.ROLES_KEY, claims.get(CommonConstant.ROLES_KEY))
                        .build();

                return Mono.just(jwt);
            } catch (Exception e) {
                return Mono.error(new JwtException(String.format(ERROR_DECODING_TOKEN_FORMAT, e.getMessage())));
            }
        };
    }

}
