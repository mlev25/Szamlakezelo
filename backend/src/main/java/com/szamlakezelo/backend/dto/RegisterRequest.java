package com.szamlakezelo.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "A név kötelező.")
    private String name;

    @NotBlank(message = "A felhasználónév kötelező.")
    private String username;

    // Erősebb jelszó szabály
    @NotBlank(message = "A jelszó kötelező.")
    @Size(min = 6, message = "A jelszónak legalább 6 karakter hosszúnak kell lennie.")
    private String password;

    // A szerepkör neve (pl. USER, BOOKKEEPER), amit választott.
    // Figyelem: A Controllerben ellenőrizzük, hogy ez csak nem-Admin szerepkör lehet!
    @NotBlank(message = "A szerepkör kötelező.")
    private String role;
}
