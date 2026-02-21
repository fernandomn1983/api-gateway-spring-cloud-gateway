package com.nurtricenter.apigateway.filter.ratelimiting;

import com.nurtricenter.apigateway._shared.constant.CommonConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Objects;

import static com.nurtricenter.apigateway.filter.ratelimiting.RateLimitingConstant.*;

@Component
@RequiredArgsConstructor
public class RateLimitingFilter implements GlobalFilter, Ordered {

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String clientIp = Objects.requireNonNull(exchange.getRequest()
                        .getRemoteAddress())
                .getAddress()
                .getHostAddress();

        String key = RATE_LIMIT_KEY + clientIp;

        return reactiveRedisTemplate.opsForValue()
                .increment(key)
                .flatMap(count -> {
                    if (count == ONE) {
                        return reactiveRedisTemplate.expire(key, Duration.ofMinutes(1)).thenReturn(count);
                    }
                    return Mono.just(count);
                })
                .flatMap(count -> {
                    exchange.getResponse()
                            .getHeaders()
                            .add(CommonConstant.X_RATE_LIMIT_LIMIT_HEADER, String.valueOf(MAX_REQUESTS_PER_MINUTE));
                    exchange.getResponse()
                            .getHeaders()
                            .add(CommonConstant.X_RATE_LIMIT_REMAINING_HEADER, String.valueOf(Math.max(0, MAX_REQUESTS_PER_MINUTE - count)));

                    if (count > MAX_REQUESTS_PER_MINUTE) {
                        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);

                        return exchange.getResponse().setComplete();
                    }

                    return chain.filter(exchange);
                });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + FIRST_PLACE;
    }

}
