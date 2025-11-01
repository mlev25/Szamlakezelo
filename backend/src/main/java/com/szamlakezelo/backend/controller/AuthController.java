package com.szamlakezelo.backend.controller;

import com.szamlakezelo.backend.data.model.User;
import com.szamlakezelo.backend.dto.JwtResponse;
import com.szamlakezelo.backend.dto.LoginRequest;
import com.szamlakezelo.backend.dto.RegisterRequest;
import com.szamlakezelo.backend.dto.UserDto;
import com.szamlakezelo.backend.security.UserPrincipal;
import com.szamlakezelo.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        String jwt = authService.authenticateUser(loginRequest);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return ResponseEntity.ok(new JwtResponse(
                jwt,
                userPrincipal.getId(),
                userPrincipal.getUsername(),
                userPrincipal.getName(),
                roles));
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {

        User user = authService.registerUser(registerRequest);

        // 2. Konvertálás a publikus UserDto-ra
        UserDto userDto = authService.mapToUserDto(user);

        // 3. Visszatérés a UserDto-val (JSON formátumban)
        return ResponseEntity.ok(userDto);
    }
}
