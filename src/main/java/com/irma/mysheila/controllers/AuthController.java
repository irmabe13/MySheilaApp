package com.irma.mysheila.controllers;

import com.irma.mysheila.dto.LoginRequest;
import com.irma.mysheila.dto.MeResponse;
import com.irma.mysheila.dto.RegisterRequest;
import com.irma.mysheila.entities.User;
import com.irma.mysheila.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Value("${app.security.cookie-name}")
    private String cookieName;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req, HttpServletResponse response) {
        authService.register(req, response);
        return ResponseEntity.ok(Map.of("message", "registered"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req, HttpServletResponse response) {
        authService.login(req, response);
        return ResponseEntity.ok(Map.of("message", "logged-in"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        String jti = (String) request.getAttribute("jti");
        authService.logout(jti, response);
        return ResponseEntity.ok(Map.of("message", "logged-out"));
    }

    @GetMapping("/me")
    public MeResponse me(@AuthenticationPrincipal User user) {
        return new MeResponse(user.getId(), user.getEmail(), user.getFirstname());
    }
}
