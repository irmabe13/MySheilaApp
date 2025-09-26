package com.irma.mysheila.services;

import com.irma.mysheila.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Utilisation de ReflectionTestUtils pour définir les champs privés
        ReflectionTestUtils.setField(jwtService, "jwtSecret", "veryLongAndSecureSecretKeyThatIsAtLeast256BitsLong");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86400000L);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", 604800000L);
    }

    @Test
    void generateAccessToken_ValidAuthentication_ReturnsToken() {
        UserDetails userDetails = new User("test@mail.com", "password", Collections.emptyList());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String token = jwtService.generateAccessToken(authentication);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        String username = jwtService.extractUsernameFromToken(token);
        assertEquals("test@mail.com", username);
    }
}
