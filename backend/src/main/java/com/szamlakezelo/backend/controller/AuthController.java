package com.szamlakezelo.backend.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.szamlakezelo.backend.data.model.User;
import com.szamlakezelo.backend.dto.*;
import com.szamlakezelo.backend.exception.CaptchaRequiredException;
import com.szamlakezelo.backend.security.UserPrincipal;
import com.szamlakezelo.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final DefaultKaptcha captchaProducer;


    public AuthController(AuthService authService, DefaultKaptcha captchaProducer) {
        this.authService = authService;
        this.captchaProducer = captchaProducer;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            String jwt = authService.authenticateUser(loginRequest, request);

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
                    userPrincipal.getLastLoginDate(),
                    roles));

        } catch (CaptchaRequiredException e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("CAPTCHA_REQUIRED", e.getMessage()));

        } catch (AuthenticationException e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("LOGIN_FAILED", "Hibás felhasználónév vagy jelszó."));
        }
    }

//    @PostMapping("/login")
//    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
//        String jwt = authService.authenticateUser(loginRequest, request);
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
//
//        List<String> roles = userPrincipal.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .toList();
//
//        return ResponseEntity.ok(new JwtResponse(
//                jwt,
//                userPrincipal.getId(),
//                userPrincipal.getUsername(),
//                userPrincipal.getName(),
//                roles));
//    }

    @GetMapping("/captcha")
    public ResponseEntity<String> generateCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String captcha = captchaProducer.createText();
        request.getSession().setAttribute("captchaCode", captcha);

        BufferedImage image = captchaProducer.createImage(captcha);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", outputStream);
        byte[] bytes = outputStream.toByteArray();
        String encoded64Image = Base64.getEncoder().encodeToString(bytes);

        return ResponseEntity.ok(encoded64Image);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {

//        User user = authService.registerUser(registerRequest);
//        UserDto userDto = authService.mapToUserDto(user);
//        return ResponseEntity.ok(userDto);

        try {
            User user = authService.registerUser(registerRequest);
            UserDto userDto = authService.mapToUserDto(user);

            return ResponseEntity.ok(userDto);

        } catch (RuntimeException ex) {

            if (ex.getMessage().contains("A felhasználónév már foglalt!")) {

                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", ex.getMessage());

                return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
            }

            return new ResponseEntity<>("Ismeretlen hiba: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
