package ru.c21501.rfcservice.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}

