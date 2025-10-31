package com.szamlakezelo.backend.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    /**
     * Ez a metódus akkor hívódik meg, ha a felhasználó hitelesítetlenül (pl. token nélkül)
     * próbál hozzáférni egy védett erőforráshoz.
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        // HTTP 401 Unauthorized státusz beállítása
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Válasz fejléc és tartalomtípus beállítása
        response.setContentType("application/json");

        // Beszédes hibaüzenet JSON formátumban
        response.getWriter().write("{ \"status\": 401, \"error\": \"Unauthorized\", \"message\": \"Hozzáférés megtagadva: Hitelesítés szükséges, vagy érvénytelen a JWT token.\" }");
    }
}