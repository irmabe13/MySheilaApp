package com.irma.mysheila.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irma.mysheila.dto.AuthRequest;
import com.irma.mysheila.dto.TokenPair;
import com.irma.mysheila.services.AuthService;

public class AuthControllerTest {

  @Mock private AuthService authService;

  @InjectMocks private AuthController authController;

  private MockMvc mvc;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    mvc = MockMvcBuilders.standaloneSetup(authController).build();
  }

  @Test
  @DisplayName("POST /api/auth/login")
  public void login() throws Exception {
    when(authService.login(any(AuthRequest.class)))
        .thenReturn(new TokenPair("access-123", "refresh-456"));
    var body = new AuthRequest("irma@test.com", "pass123");
    mvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value("access-123"))
        .andExpect(jsonPath("$.refreshToken").value("refresh-456"));
  }
}
