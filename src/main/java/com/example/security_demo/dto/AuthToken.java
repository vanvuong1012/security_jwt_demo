package com.example.security_demo.dto;

import lombok.Data;

@Data
public class AuthToken {
    private String username;
    private String token;

    public AuthToken(String username, String token) {
        this.username = username;
        this.token = token;
    }
}