package com.szamlakezelo.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "A felhasználónév nem lehet üres.")
    private String username;

    @NotBlank(message = "A jelszó nem lehet üres.")
    private String password;

    private String captchaAnswer;
}
