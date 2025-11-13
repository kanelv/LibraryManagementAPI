package com.kane.librarymanagement.application.user.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest {
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
  @Size(max = 6)
  private String password;

  @Size(max = 50)
  private String firstName;

  @Size(max = 50)
  private String lastName;

  @Size(max = 255)
  private String address;
}
