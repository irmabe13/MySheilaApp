package com.irma.mysheila.services;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import com.irma.mysheila.entities.User;

public class JwtServiceTest {

  private JwtService jwt;

  @BeforeEach
  void setUp() {
    jwt = new JwtService();
    // Utilisation de ReflectionTestUtils pour définir les champs privés
    ReflectionTestUtils.setField(
        jwt, "jwtSecret", "veryLongAndSecureSecretKeyThatIsAtLeast256BitsLong");
    ReflectionTestUtils.setField(jwt, "jwtExpiration", 86400000L);
    ReflectionTestUtils.setField(jwt, "refreshExpiration", 604800000L);
  }

  @Test
  void generate_and_validate_token() {
    var fakeUser = new User();
    fakeUser.setEmail("irma@test.com");

    Authentication auth =
        new UsernamePasswordAuthenticationToken(fakeUser, null, fakeUser.getAuthorities());

    String token = jwt.generateAccessToken(auth);

    assertThat(token).isNotBlank();
    assertThat(jwt.extractUsernameFromToken(token)).isEqualTo("irma@test.com");
    assertThat(jwt.isValidToken(token)).isTrue();
  }
}
