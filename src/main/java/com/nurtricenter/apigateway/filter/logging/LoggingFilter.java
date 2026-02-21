package com.nurtricenter.apigateway.filter.logging;

import com.nurtricenter.apigateway._shared.constant.CommonConstant;
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

import static com.nurtricenter.apigateway.filter.logging.LoggingConstant.*;

@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + SECOND_PLACE)
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String requestId = UUID.randomUUID().toString();

        ServerHttpRequest mutatedRequest = request.mutate()
                .header(CommonConstant.X_REQUEST_ID_HEADER, requestId)
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        log.info(REQUEST_DETAILS_FORMAT_LOG,
                requestId,
                request.getMethod(),
                request.getPath(),
                request.getHeaders(),
                request.getRemoteAddress()

        );

        long startTime = System.currentTimeMillis();

        return chain.filter(mutatedExchange)
                .doOnSuccess(response -> logResponse(mutatedExchange.getResponse(), requestId, startTime))
                .doOnError(error -> log.error(ERROR_PROCESSING_REQUEST_LOG, error.getMessage()))
                .onErrorResume(error -> {
                    log.error(REQUEST_FAILED_LOG, error.getMessage());
                    return Mono.error(error);
                });
    }

    private void logResponse(ServerHttpResponse response, String requestId, long startTime) {
        long duration = System.currentTimeMillis() - startTime;

        log.info(RESPONSE_DETAILS_FORMAT_LOG,
                requestId,
                response.getStatusCode(),
                duration
        );
        try {
            response.getHeaders().add(X_RESPONSE_TIME_HEADER, duration + MILLISECONDS);
            response.getHeaders().add(CommonConstant.X_REQUEST_ID_HEADER, requestId);
        } catch (UnsupportedOperationException e) {
            log.debug(CANNOT_MODIFY_RESPONSE_HEADERS_RESPONSE_ALREADY_COMMITTED_LOG);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + SECOND_PLACE;
    }

}
