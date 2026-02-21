package com.nurtricenter.apigateway.security.controller;

import com.nurtricenter.apigateway._shared.constant.CommonConstant;
import com.nurtricenter.apigateway.security.dto.AuthRequestDto;
import com.nurtricenter.apigateway.security.dto.AuthResponseDto;
import com.nurtricenter.apigateway.security.service.JwtService;
import com.nurtricenter.apigateway.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static com.nurtricenter.apigateway.security.constant.AuthenticationConstant.*;

@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthenticationController {

    private final ReactiveAuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ReactiveUserDetailsService userDetailsService;

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponseDto>> login(@RequestBody AuthRequestDto request) {
        log.info(AUTHENTICATION_ATTEMPT_USER_LOG, request.getUsername());

        return authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                request.getPassword()
                        )
                )
                .flatMap(authentication -> {
                    log.info(AUTHENTICATION_SUCCESSFUL_USER_LOG, request.getUsername());

                    return userDetailsService.findByUsername(request.getUsername())
                            .map(userDetails -> {
                                String token = jwtService.generateToken(userDetails);

                                Map<String, Object> userInfo = new HashMap<>();
                                userInfo.put(USERNAME_KEY, userDetails.getUsername());
                                userInfo.put(CommonConstant.ROLES_KEY, userDetails.getAuthorities());

                                if (userDetails instanceof UserDetailsImpl userDetailsImpl) {
                                    userInfo.put(CommonConstant.EMAIL_KEY, userDetailsImpl.getEmail());
                                    userInfo.put(CommonConstant.NAME_KEY, userDetailsImpl.getName());
                                }

                                AuthResponseDto responseDto = AuthResponseDto.builder()
                                        .token(token)
                                        .type(BEARER_TYPE)
                                        .expiresIn(jwtService.extractExpiration(token).getTime())
                                        .userInfo(userInfo)
                                        .build();

                                return ResponseEntity.ok(responseDto);
                            });
                })
                .onErrorResume(e -> {
                    log.error(AUTHENTICATION_FAILED_USER_ERROR_LOG, request.getUsername(), e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(AuthResponseDto.builder()
                                    .error(INVALID_USERNAME_PASSWORD_MSG)
                                    .build()));
                });

    }

}
