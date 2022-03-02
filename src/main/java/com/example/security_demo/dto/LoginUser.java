package com.example.security_demo.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class LoginUser {

//    @NotEmpty(message = "Not username")
    private String username;

//    @NotEmpty(message = "Not password")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
