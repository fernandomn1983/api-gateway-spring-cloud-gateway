package com.nurtricenter.apigateway.filter.ratelimiting;

import com.nurtricenter.apigateway._shared.constant.CommonConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class RateLimitingFilter implements GlobalFilter, Ordered {

	private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String clientIp = Objects.requireNonNull(exchange.getRequest()
				.getRemoteAddress())
			.getAddress()
			.getHostAddress();

		String key = RATE_LIMIT_KEY + clientIp;

		log.info(RATE_LIMITING_KEY_LOG, key);

		return reactiveRedisTemplate.opsForValue()
			.increment(key)
			.flatMap(count -> {
				log.info(INCREMENTED_COUNT_KEY_LOG, key, count);
				return reactiveRedisTemplate.expire(key, Duration.ofMinutes(1)).thenReturn(count);
			})
			.flatMap(count -> {
				log.info(FINAL_COUNT_KEY_LOG, key, count);
				exchange.getResponse()
					.getHeaders()
					.add(CommonConstant.X_RATE_LIMIT_LIMIT_HEADER, String.valueOf(MAX_REQUESTS_PER_MINUTE));
				exchange.getResponse()
					.getHeaders()
					.add(CommonConstant.X_RATE_LIMIT_REMAINING_HEADER, String.valueOf(Math.max(0, MAX_REQUESTS_PER_MINUTE - count)));

				if (count > MAX_REQUESTS_PER_MINUTE) {
					log.warn(RATE_LIMIT_EXCEEDED_KEY_LOG, key, count, MAX_REQUESTS_PER_MINUTE);
					exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);

					return exchange.getResponse().setComplete();
				}

				log.info(ALLOWING_REQUEST_KEY_LOG, key, count);
				return chain.filter(exchange);
			})
			.onErrorResume(throwable -> {
				log.error(ERROR_RATE_LIMITING_KEY_LOG, key, throwable.getMessage());
				// Allow the request if Redis fails
				return chain.filter(exchange);
			});
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE + FIRST_PLACE;
	}

}
