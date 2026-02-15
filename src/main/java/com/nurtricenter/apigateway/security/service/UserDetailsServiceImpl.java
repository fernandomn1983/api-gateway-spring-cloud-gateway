package com.nurtricenter.apigateway.security.service;

import com.nurtricenter.apigateway.security.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return Mono.fromCallable(() -> new UserDetailsImpl(getValidUsername(username)));
    }

    public UserDto getValidUsername(String username) {
        if (username == null || !username.equals("validUser")) {
            return null;
        }

        return new UserDto(username);
    }


}
