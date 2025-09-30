package com.irma.mysheila.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.irma.mysheila.entities.Role;
import com.irma.mysheila.entities.User;
import com.irma.mysheila.repositories.RoleRepository;
import com.irma.mysheila.repositories.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SecurityIntegrationTest {

  @Autowired MockMvc mvc;
  @Autowired UserRepository userRepository;
  @Autowired RoleRepository roleRepository;
  @Autowired PasswordEncoder passwordEncoder;

  int userId;
  String email = "secure@test.com";

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
    roleRepository.deleteAll();
    var u = new User();
    u.setEmail(email);
    u.setPassword(passwordEncoder.encode("pass123"));
    u.setEnabled(true);
    u.setFirstname("Secure");
    u.setLastname("Test");

    var role = new Role();
    role.setName("USER");
    role = roleRepository.save(role);

    u.setRole(role);

    userId = userRepository.save(u).getIdUser();
  }

  @Test
  void me_requires_auth() throws Exception {
    mvc.perform(get("/api/users/me")).andExpect(status().isUnauthorized());
  }
}
