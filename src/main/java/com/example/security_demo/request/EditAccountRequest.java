package com.example.security_demo.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class EditAccountRequest {
    @JsonIgnore
    private Long userId;
    private String password;
}
