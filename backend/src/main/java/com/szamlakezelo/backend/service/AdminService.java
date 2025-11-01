package com.szamlakezelo.backend.service;

import com.szamlakezelo.backend.data.model.Role;
import com.szamlakezelo.backend.data.model.User;
import com.szamlakezelo.backend.data.repository.RoleRepository;
import com.szamlakezelo.backend.data.repository.UserRepository;
import com.szamlakezelo.backend.dto.UserDto;
import com.szamlakezelo.backend.util.RoleName;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AdminService(UserRepository userRepository, RoleRepository roleRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    //1. osszes felhasznalo listazasa
    public List<UserDto> getAllUsers(){
        return userRepository.findAll()
                .stream()
                .map(AuthService::mapToUserDto)
                .collect(Collectors.toList());
    }

    //2. uj szerepkorok beallitasa
    public UserDto setRolesForUser(Long userId, Set<String> newRoles) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Felhasznalo nem talalhato"));

        if (newRoles == null || newRoles.isEmpty()) {
            throw new RuntimeException("Nem lehet ures az uj jogosultsag set!");
        }

        Set<Role> rolesToAssign = new HashSet<>();
        for (String newRole : newRoles) {
            if (roleRepository.findByName(RoleName.valueOf(newRole)) != null) {
                rolesToAssign.add(roleRepository.findByName(RoleName.valueOf(newRole)));
            }
        }

        user.setRoles(rolesToAssign);
        User updatedUser = userRepository.save(user);

        return AuthService.mapToUserDto(updatedUser);
    }

    //3. felhasznalo torlese
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Felhasznalo nem talalhato az id alapjan: " + userId);
        }
        userRepository.deleteById(userId);
    }


    //2. szerepkor hozzaadasa, vegul nem ez kell sztem, hanem a full feluliras
//    public UserDto addRoleToUser(Long userId, RoleName roleName){
//        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
//
//        Role role = roleRepository.findByName(roleName);
//
//        user.getRoles().add(role);
//        User updatedUser = userRepository.save(user);
//        return AuthService.mapToUserDto(updatedUser);
//
//    }
}
