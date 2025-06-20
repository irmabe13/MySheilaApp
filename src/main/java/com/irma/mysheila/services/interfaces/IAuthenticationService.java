package com.irma.mysheila.services.interfaces;

import com.irma.mysheila.dto.authentication.LoginRequest;
import com.irma.mysheila.dto.authentication.RegisterRequest;
import org.springframework.http.ResponseEntity;

public interface IAuthenticationService {
    ResponseEntity<String> register(RegisterRequest request);
    ResponseEntity<String> login(LoginRequest request);
}
