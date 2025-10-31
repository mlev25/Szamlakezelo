package com.szamlakezelo.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// Használjuk a Lombok @Data és @NoArgsConstructor annotációit
@Data
@NoArgsConstructor
public class JwtResponse {

    // A JWT token, amit a további kérésekhez a headerben küldünk
    private String token;

    // A token típusa (standardban mindig "Bearer")
    private String type = "Bearer";

    // Felhasználói adatok
    private Long id;
    private String username;
    private String name;

    // A felhasználó szerepkörei (pl. [ "ROLE_ADMINISTRATOR", "ROLE_BOOKKEEPER" ])
    private List<String> roles;

    /**
     * Konstruktor a kényelmes létrehozáshoz az AuthService-ben.
     *
     * @param accessToken A generált JWT token.
     * @param id          A felhasználó egyedi ID-ja.
     * @param username    A felhasználónév.
     * @param name        A felhasználó teljes neve.
     * @param roles       A felhasználó szerepkörei.
     */
    public JwtResponse(String accessToken, Long id, String username, String name, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.name = name;
        this.roles = roles;
    }
}