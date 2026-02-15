package com.nurtricenter.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
public class LoggingFilter implements GlobalFilter, Ordered {


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String requestId = UUID.randomUUID().toString();

        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-Request-Id", requestId)
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        log.info("""
                        Request Details:
                        requestId = {}
                        method = {}
                        path = {}
                        headers = {}
                        clientIp = {}
                        """,
                requestId,
                request.getMethod(),
                request.getPath(),
                request.getHeaders(),
                request.getRemoteAddress()

        );

        long startTime = System.currentTimeMillis();

        return chain.filter(mutatedExchange)
                .doOnSuccess(response -> logResponse(mutatedExchange.getResponse(), requestId, startTime))
                .doOnError(error -> log.error("Error processing request: {}", error.getMessage()))
                .onErrorResume(error -> {
                    log.error("Request failed: {}", error.getMessage());
                    return Mono.error(error);
                });
    }

    private void logResponse(ServerHttpResponse response, String requestId, long startTime) {
        long duration = System.currentTimeMillis() - startTime;

        log.info("""
                        Response Details:
                        requestId = {}
                        statusCode = {}
                        duration = {} ms
                        """,
                requestId,
                response.getStatusCode(),
                duration
        );
        try {
            response.getHeaders().add("X-Response-Time", duration + "ms");
            response.getHeaders().add("X-Request-Id", requestId);
        } catch (UnsupportedOperationException e) {
            log.debug("Cannot modify response headers - response already committed");
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 2;
    }

}
