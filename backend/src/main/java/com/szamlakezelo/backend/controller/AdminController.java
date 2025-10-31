package com.szamlakezelo.backend.controller;

import com.szamlakezelo.backend.data.model.Role;
import com.szamlakezelo.backend.dto.RoleUpdateDto;
import com.szamlakezelo.backend.dto.UserDto;
import com.szamlakezelo.backend.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

//csak az admin erheti el az egesz endpointot
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    //1. userek listazasa
    // GET api/admin/users
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    //2. szerepkorok beallitasa multiselect alapjan
    // PUT /api/admin/users/{userID}/roles
    @PutMapping("/users/{userId}/roles")
    public ResponseEntity<UserDto> setRoles(@PathVariable Long userId, @Valid @RequestBody RoleUpdateDto roleDto) {
        UserDto updatedUser = adminService.setRolesForUser(userId, roleDto.getRoles());
        return ResponseEntity.ok(updatedUser);
    }

    //3. felhasznalo torlese
    // DELETE /api/admin/users/{userId}
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<UserDto> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
