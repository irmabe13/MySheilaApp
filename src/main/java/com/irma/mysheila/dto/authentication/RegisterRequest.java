package com.irma.mysheila.dto.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;



public record RegisterRequest (
        @NotNull @NotBlank @JsonProperty("email") String email,
        @NotNull @NotBlank @JsonProperty("password") String password,
        @NotNull @NotBlank @JsonProperty("first_name") String firstName,
        @NotNull @NotBlank @JsonProperty("last_name") String lastName,
        @NotNull @NotBlank @JsonProperty("birth_date") String birthDate,
        @NotNull @NotBlank @JsonProperty("gender") String gender
) {
}
