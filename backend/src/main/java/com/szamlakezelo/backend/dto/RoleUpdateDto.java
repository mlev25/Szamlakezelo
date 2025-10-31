package com.szamlakezelo.backend.dto;

import com.szamlakezelo.backend.data.model.Role;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.Set;

@Data
public class RoleUpdateDto {
    @NotEmpty(message = "Egy felhasznalo legalabb egy jogosultsaggal rendelkezik!")
    @UniqueElements
    private Set<String> roles;
}
