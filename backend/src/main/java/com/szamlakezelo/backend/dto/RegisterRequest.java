package com.szamlakezelo.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "A nev kotelezo.")
    private String name;

    @NotBlank(message = "A felhasznalonev kotelezo.")
    private String username;

    @NotBlank(message = "A jelszo kotelezo.")
    @Size(min = 6, message = "A jelszo legalabb 6 karakter hosszu.")
    private String password;

    @NotBlank(message = "Kotelezo a szerepkor.")
    private String role;
}
