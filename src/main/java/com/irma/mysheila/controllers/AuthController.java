package com.irma.mysheila.controllers;

import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.irma.mysheila.dto.AuthRequest;
import com.irma.mysheila.dto.RefreshTokenRequest;
import com.irma.mysheila.dto.RegisterRequest;
import com.irma.mysheila.dto.TokenPair;
import com.irma.mysheila.services.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
    TokenPair tokenPair = authService.login(request);
    return ResponseEntity.ok(tokenPair);
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
    authService.register(request);
    return ResponseEntity.ok("User registered successfully");
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
    TokenPair tokenPair = authService.refreshToken(request);
    return ResponseEntity.ok(tokenPair);
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout() {
    authService.logout();
    return ResponseEntity.ok("You have been signed out");
  }
}
