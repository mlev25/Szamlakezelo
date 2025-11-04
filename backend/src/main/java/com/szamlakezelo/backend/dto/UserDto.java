package com.szamlakezelo.backend.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String name;
    private List<String> roles;
    private Date lastLoginDate;
}