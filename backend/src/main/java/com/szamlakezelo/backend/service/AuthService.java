package com.szamlakezelo.backend.service;

import com.google.code.kaptcha.impl.DefaultKaptcha;
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

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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

    private final DefaultKaptcha captchaProducer;

    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository,
                       RoleRepository roleRepository, PasswordEncoder encoder, JwtUtils jwtUtils, DefaultKaptcha captchaProducer) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
        this.captchaProducer = captchaProducer;
    }


    public String authenticateUser(LoginRequest loginRequest, HttpServletRequest request) {

        User user = userRepository.findByUsername(loginRequest.getUsername());
        final int CAPTCHA_LIMIT = 3;

        if (user != null && user.getFailedLoginAttempts() >= CAPTCHA_LIMIT) {

            String expectedCaptcha = (String) request.getSession().getAttribute("captchaCode");

            if (expectedCaptcha == null || !expectedCaptcha.equalsIgnoreCase(loginRequest.getCaptchaAnswer())) {

                handleLoginFailure(user);
                throw new BadCredentialsException("CAPTCHA ellenorzes sikertelen.");
            }
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            if (user != null) {
                handleLoginSuccess(user);
            } else {
                user = userRepository.findByUsername(loginRequest.getUsername());
                if (user != null) handleLoginSuccess(user);
            }

            SecurityContextHolder.getContext().setAuthentication(authentication);

            return jwtUtils.generateJwtToken((UserPrincipal) authentication.getPrincipal());

        } catch (AuthenticationException e) {

            if (user != null) {
                handleLoginFailure(user);
            } else if (userRepository.existsByUsername(loginRequest.getUsername())) {
                User failedUser =  userRepository.findByUsername(loginRequest.getUsername());
                handleLoginFailure(failedUser);
            }

            throw e;
        }
//        try {
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
//
//            if (userRepository.existsByUsername(loginRequest.getUsername())) {
//                User user =  userRepository.findByUsername(loginRequest.getUsername());
//                user.setFailedLoginAttempts(0);
//                user.setLastLoginDate(new Date());
//                userRepository.save(user);
//            }
//
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//
//            return jwtUtils.generateJwtToken((UserPrincipal) authentication.getPrincipal());
//
//        } catch (AuthenticationException e) {
//            if (userRepository.existsByUsername(loginRequest.getUsername())) {
//                User user =  userRepository.findByUsername(loginRequest.getUsername());
//                user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
//                userRepository.save(user);
//            }
//
//            throw e;
//        }
    }

    private void handleLoginFailure(User user) {
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        userRepository.save(user);
    }

    private void handleLoginSuccess(User user) {
        user.setFailedLoginAttempts(0);
        user.setLastLoginDate(new Date());
        userRepository.save(user);
    }

    public User registerUser(RegisterRequest signUpRequest) {

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new RuntimeException("Error: A felhasználónév már foglalt!");
        }

        User user = new User();
        user.setName(signUpRequest.getName());
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));

        Set<Role> roles = new HashSet<>();
        String strRole = signUpRequest.getRole().toUpperCase();

        RoleName roleName;
        try {
            roleName = RoleName.valueOf(strRole);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error: A szerepkör nem létezik: " + strRole);
        }

        Role userRole = roleRepository.findByName(roleName);

        roles.add(userRole);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    public static UserDto mapToUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setName(user.getName());

        List<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList());

        dto.setRoles(roleNames);
        return dto;
    }
}