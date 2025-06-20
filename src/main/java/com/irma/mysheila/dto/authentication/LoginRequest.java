package com.irma.mysheila.dto.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotNull @NotBlank @JsonProperty("email") String email,
        @NotNull @NotBlank @JsonProperty("password") String password
) {

}
