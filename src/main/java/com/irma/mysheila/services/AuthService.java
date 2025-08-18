package com.irma.mysheila.services;


import com.irma.mysheila.config.security.CookieUtil;
import com.irma.mysheila.config.security.JwtService;
import com.irma.mysheila.dto.authentication.LoginRequest;
import com.irma.mysheila.dto.authentication.RegisterRequest;
import com.irma.mysheila.entities.Role;
import com.irma.mysheila.entities.Token;
import com.irma.mysheila.entities.User;
import com.irma.mysheila.repositories.RoleRepository;
import com.irma.mysheila.repositories.TokenRepository;
import com.irma.mysheila.repositories.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final TokenRepository tokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final CookieUtil cookieUtil;

    public void register(RegisterRequest req, HttpServletResponse response) {
        if (userRepo.existsByEmail(req.email())) {
            throw new IllegalArgumentException("Email already registered");
        }
        Role userRole = roleRepo.findByName("USER").orElseThrow();
        User user = User.builder()
                .email(req.email())
                .passwordHash(passwordEncoder.encode(req.password()))
                .firstname(req.firstname())
                .createdAt(Instant.now())
                .roles(Set.of(userRole))
                .build();
        userRepo.save(user);
        issueCookieFor(user, response);
    }

    public void login(LoginRequest req, HttpServletResponse response) {
        Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(req.email(), req.password()));
        User user = (User) auth.getPrincipal();
        issueCookieFor(user, response);
    }

    public void logout(String jti, HttpServletResponse response) {
        tokenRepo.findByJti(jti).ifPresent(t -> { t.setRevoked(true); tokenRepo.save(t); });
        cookieUtil.clearToken(response);
    }

    private void issueCookieFor(User user, HttpServletResponse response) {
        String jti = jwtService.newJti();
        String rolesCsv = user.getRoles().stream().map(Role::getName).collect(Collectors.joining(","));
        String token = jwtService.generateToken(user.getEmail(), rolesCsv, jti);
        Token t = Token.builder().jti(jti).user(user).revoked(false).createdAt(Instant.now()).build();
        tokenRepo.save(t);
        cookieUtil.writeToken(response, token, jwtService.getCookieMaxAgeSeconds());
    }

}
