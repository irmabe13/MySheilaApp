package com.irma.mysheila.controllers;

import com.irma.mysheila.dto.authentication.LoginRequest;
import com.irma.mysheila.dto.authentication.RegisterRequest;
import com.irma.mysheila.services.interfaces.IAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private final IAuthenticationService authenticationService;

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> register(
            @Validated @RequestBody RegisterRequest request
    ) {
        return this.authenticationService.register(request);
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> login(
            @Validated @RequestBody LoginRequest request
    ){
        return this.authenticationService.login(request);
    }

}
