package com.kane.librarymanagement.application.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
  @NotBlank(message = "Username is required")
  @Size(max = 100)
  private String username;

  @NotBlank(message = "phoneNumber is required")
  @Size(max = 15)
  private String phoneNumber;

  @NotBlank(message = "email is required")
  @Size(max = 50)
  private String email;

  @NotBlank(message = "password is required")
  @Size(min = 6, message = "Password must be at least 6 characters")
  private String password;

  @Size(max = 50)
  private String firstName;

  @Size(max = 50)
  private String lastName;

  @Size(max = 255)
  private String address;
}
