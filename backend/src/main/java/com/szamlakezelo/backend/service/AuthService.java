package com.szamlakezelo.backend.service;

import com.szamlakezelo.backend.dto.LoginRequest;
import com.szamlakezelo.backend.dto.RegisterRequest;
import com.szamlakezelo.backend.data.model.Role;
import com.szamlakezelo.backend.data.model.User;
import com.szamlakezelo.backend.data.repository.RoleRepository;
import com.szamlakezelo.backend.data.repository.UserRepository;
import com.szamlakezelo.backend.dto.UserDto;
import com.szamlakezelo.backend.security.UserPrincipal;
import com.szamlakezelo.backend.security.JwtUtils;
import com.szamlakezelo.backend.util.RoleName;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    // Konstruktor injektálás: FÜGGŐSÉG BEFECSKENDEZÉS
    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository,
                       RoleRepository roleRepository, PasswordEncoder encoder, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Hitelesíti a felhasználót, beállítja a Security Context-et, és generálja a JWT-t.
     * @param loginRequest A bejelentkezési adatok (username, password).
     * @return A generált JWT Token.
     */
    public String authenticateUser(LoginRequest loginRequest) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            if (userRepository.existsByUsername(loginRequest.getUsername())) {
                User user =  userRepository.findByUsername(loginRequest.getUsername());
                user.setFailedLoginAttempts(0);
                user.setLastLoginDate(new Date());
                userRepository.save(user);
            }

            SecurityContextHolder.getContext().setAuthentication(authentication);

            return jwtUtils.generateJwtToken((UserPrincipal) authentication.getPrincipal());

        } catch (AuthenticationException e) {
            if (userRepository.existsByUsername(loginRequest.getUsername())) {
                User user =  userRepository.findByUsername(loginRequest.getUsername());
                user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
                userRepository.save(user);
            }

            throw e;
        }
    }

    /**
     * Regisztrál egy új felhasználót és elmenti az adatbázisba.
     * @param signUpRequest A regisztrációs adatok (name, username, password, role).
     * @return Az újonnan létrehozott User entitás.
     */
    public User registerUser(RegisterRequest signUpRequest) {

        // 1. Ellenőrizzük, hogy a felhasználónév foglalt-e
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new RuntimeException("Error: A felhasználónév már foglalt!");
        }

        // 2. Új User entitás létrehozása és a jelszó titkosítása
        User user = new User();
        user.setName(signUpRequest.getName());
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(encoder.encode(signUpRequest.getPassword())); // Jelszó hashelése

        // 3. Szerepkörök beállítása (Role hozzárendelés)
        Set<Role> roles = new HashSet<>();
        String strRole = signUpRequest.getRole().toUpperCase();

        RoleName roleName;
        try {
            roleName = RoleName.valueOf(strRole); // Konvertálás Enum-ra
        } catch (IllegalArgumentException e) {
            // Ha a megadott szerepkör nem létezik (pl. "ADMINISTRATOR" helyett "admin")
            throw new RuntimeException("Error: A szerepkör nem létezik: " + strRole);
        }

        Role userRole = roleRepository.findByName(roleName);

        roles.add(userRole);
        user.setRoles(roles);

        // 4. Mentés az adatbázisba
        return userRepository.save(user);
    }

    public static UserDto mapToUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setName(user.getName());

        // Szerepkörök String listává alakítása
        List<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList());

        dto.setRoles(roleNames);
        return dto;
    }
}