package com.nurtricenter.apigateway.security.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserDto {

    private String username;
    private String password;
    private String email;
    private String name;
    private Set<String> roles;
    private boolean enabled;

    public UserDto(String username) {
        this.username = username;
        // Hardcoding user data for testing purposes
        this.password = "$2a$12$BSCGhRHMNx6Lm5V.EcaXjO8yVAo68DSh7iSkCqeHk/qBBZU54fwfi";
        this.email = "validuser@email.com";
        this.name = "Valid User";
        this.roles = Set.of("USER", "ADMIN");
        this.enabled = true;
    }

}
