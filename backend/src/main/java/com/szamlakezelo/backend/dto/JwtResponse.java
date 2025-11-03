package com.szamlakezelo.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class JwtResponse {

    private String token;

    private String type = "Bearer";

    private Long id;
    private String username;
    private String name;
    private Date lastLoginDate;

    private List<String> roles;


    public JwtResponse(String accessToken, Long id, String username, String name, Date lastLoginDate,List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.name = name;
        this.roles = roles;
        this.lastLoginDate = lastLoginDate;
    }
}