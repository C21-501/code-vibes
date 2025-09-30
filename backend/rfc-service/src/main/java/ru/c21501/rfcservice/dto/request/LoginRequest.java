package ru.c21501.rfcservice.dto.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}

