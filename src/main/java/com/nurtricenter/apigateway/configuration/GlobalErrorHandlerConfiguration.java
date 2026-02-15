package com.nurtricenter.apigateway.configuration;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Configuration
@Order(-1)
public class GlobalErrorHandlerConfiguration implements ErrorWebExceptionHandler {


    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        if (ex instanceof NotFoundException) {
            response.setStatusCode(HttpStatus.NOT_FOUND);

            return handleError(response, "Not Found", "The requested resource was not found.");
        } else if (ex instanceof ResponseStatusException responseStatusException) {
            response.setStatusCode(responseStatusException.getStatusCode());

            return handleError(response, responseStatusException.getStatusCode().toString(), responseStatusException.getReason());
        }

        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return handleError(response, "Internal Server Error", "An unexpected error occurred.");
    }

    private Mono<Void> handleError(ServerHttpResponse response, String error, String message) {
        String body = """
                {
                    "error": "%s",
                    "message": "%s",
                    "timestamp": "%s"
                }
                """.formatted(error, message, LocalDateTime.now());

        return writeResponse(response, body);
    }

    private Mono<Void> writeResponse(ServerHttpResponse response, String body) {
        DataBuffer buffer = response.bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }

}
