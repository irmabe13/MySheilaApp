package com.irma.mysheila.services;

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
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public void register(@Valid RegisterRequest request) {
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

    public TokenPair login(@Valid AuthRequest request) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        TokenPair tokenPair = jwtService.generateTokenPair(authentication);

        User user =
                userRepository
                        .findByEmail(request.getEmail())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found after successful login"));

        revokeAllUserTokens(user);
        saveUserToken(user, tokenPair.getAccessToken());

        return tokenPair;
    }

    public TokenPair refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        String userEmail = jwtService.extractUsernameFromToken(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

        if (userDetails == null) {
            throw new IllegalArgumentException("User not found");
        }

        Optional<Token> storedRefreshToken =
                tokenRepository.findByTokenAndTokenType(refreshToken, TokenType.REFRESH);

        if (storedRefreshToken.isEmpty() || storedRefreshToken.get().isRevoked() || storedRefreshToken.get().isExpired()) {
            throw new IllegalArgumentException("Refresh token was revoked or expired");
        }

        if (!jwtService.validateTokenForUser(refreshToken, userDetails)) {
            throw new IllegalArgumentException("Invalid refresh token for user");
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String accessToken = jwtService.generateAccessToken(authentication);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        revokeAllUserTokens(user); // Révoque les anciens Access Tokens
        saveUserToken(user, accessToken);

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
                .ifPresent(this::revokeAllUserTokens);

        SecurityContextHolder.clearContext();
    }

    private void revokeAllUserTokens(User user) {
        // Cherche tous les tokens de l'utilisateur qui ne sont ni expirés ni révoqués
        var validUserTokens = tokenRepository.findAllValidTokensByUser(user.getIdUser());
        if (validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    // Ajouté : Enregistre le nouveau token dans la BDD
    private void saveUserToken(User user, String jwtToken) {
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
    }
}
