package com.irma.mysheila.dto.authentication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

public record LoginRequest(
        @Email @NotBlank String email,
        @NotBlank String password
) {
}
