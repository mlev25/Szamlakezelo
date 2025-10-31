package com.szamlakezelo.backend.data.repository;

import com.szamlakezelo.backend.data.model.Role;
import com.szamlakezelo.backend.util.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(RoleName roleName);
}
