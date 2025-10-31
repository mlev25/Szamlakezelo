package com.szamlakezelo.backend.data.model;

import com.szamlakezelo.backend.util.RoleName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleName name;

    @Column
    private String description;
}
