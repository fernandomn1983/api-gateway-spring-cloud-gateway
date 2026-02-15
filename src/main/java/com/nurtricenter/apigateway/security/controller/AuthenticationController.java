package com.nurtricenter.apigateway.security.controller;

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
        log.info("Login attempt for user: {}", request.getUsername());

        return authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                request.getPassword()
                        )
                )
                .flatMap(authentication -> {
                    log.info("Authentication successful for user: {}", request.getUsername());

                    return userDetailsService.findByUsername(request.getUsername())
                            .map(userDetails -> {
                                String token = jwtService.generateToken(userDetails);

                                Map<String, Object> userInfo = new HashMap<>();
                                userInfo.put("username", userDetails.getUsername());
                                userInfo.put("roles", userDetails.getAuthorities());

                                if (userDetails instanceof UserDetailsImpl userDetailsImpl) {
                                    userInfo.put("email", userDetailsImpl.getEmail());
                                    userInfo.put("name", userDetailsImpl.getName());
                                }

                                AuthResponseDto responseDto = AuthResponseDto.builder()
                                        .token(token)
                                        .type("Bearer")
                                        .expiresIn(jwtService.extractExpiration(token).getTime())
                                        .userInfo(userInfo)
                                        .build();

                                return ResponseEntity.ok(responseDto);
                            });
                })
                .onErrorResume(e -> {
                    log.error("Login failed for user: {}: {}", request.getUsername(), e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(AuthResponseDto.builder()
                                    .error("Invalid username and/or password")
                                    .build()));
                });

    }

}
