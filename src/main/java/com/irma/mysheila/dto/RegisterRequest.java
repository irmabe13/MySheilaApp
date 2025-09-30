package com.irma.mysheila.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

  @NotBlank @Email private String email;

  @NotBlank
  @Size(min = 6, max = 72)
  private String password;

  @NotBlank private String firstname;
  @NotBlank private String lastname;
}
