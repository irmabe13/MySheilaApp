package com.irma.mysheila.services;

import lombok.AllArgsConstructor;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.irma.mysheila.dto.AuthRequest;
import com.irma.mysheila.dto.RefreshTokenRequest;
import com.irma.mysheila.dto.RegisterRequest;
import com.irma.mysheila.dto.TokenPair;
import com.irma.mysheila.entities.Role;
import com.irma.mysheila.entities.Token;
import com.irma.mysheila.entities.User;
import com.irma.mysheila.enums.TokenType;
import com.irma.mysheila.exceptions.ResourceNotFoundException;
import com.irma.mysheila.repositories.RoleRepository;
import com.irma.mysheila.repositories.TokenRepository;
import com.irma.mysheila.repositories.UserRepository;

@Service
@AllArgsConstructor
@Transactional
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;

    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        Role role =
                roleRepository.findByName("USER").orElseThrow(() -> new RuntimeException("Role Not Found"));

        User user =
                User.builder()
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .firstname(request.getFirstname())
                        .lastname(request.getLastname())
                        .role(role)
                        .enabled(true)
                        .build();

        userRepository.save(user);
    }

    public TokenPair login(AuthRequest request) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user =
                userRepository
                        .findByEmail(request.getEmail())
                        .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        tokenRepository.revokeAllActiveByIdUser(user.getIdUser());

        TokenPair tokenPair = jwtService.generateTokenPair(authentication);

        tokenRepository.save(
                Token.builder()
                        .user(user)
                        .token(tokenPair.getAccessToken())
                        .tokenType(TokenType.BEARER)
                        .revoked(false)
                        .expired(false)
                        .build());

        tokenRepository.save(
                Token.builder()
                        .user(user)
                        .token(tokenPair.getRefreshToken())
                        .tokenType(TokenType.REFRESH)
                        .revoked(false)
                        .expired(false)
                        .build());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return tokenPair;
    }

    public TokenPair refreshToken(@Valid RefreshTokenRequest request) {

        String refreshToken = request.getRefreshToken();

        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        String user = jwtService.extractUsernameFromToken(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(user);

        if (userDetails == null) {
            throw new IllegalArgumentException("User not found");
        }

        Optional<Token> storedRefreshToken =
                tokenRepository.findByTokenAndTokenTypeAndRevokedFalseAndExpiredFalse(
                        refreshToken, TokenType.REFRESH);
        if (storedRefreshToken.isEmpty()) {
            throw new IllegalArgumentException("Refresh token was revoked");
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String accessToken = jwtService.generateAccessToken(authentication);
        return new TokenPair(accessToken, refreshToken);
    }

    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            return;
        }

        userRepository
                .findByEmail(userDetails.getUsername())
                .ifPresent(user -> tokenRepository.revokeAllActiveByIdUser(user.getIdUser()));

        SecurityContextHolder.clearContext();
    }
}
