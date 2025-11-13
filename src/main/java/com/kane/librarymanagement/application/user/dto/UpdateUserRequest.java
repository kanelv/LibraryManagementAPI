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
public class UpdateUserRequest {
  @NotBlank(message = "phoneNumber is required")
  @Size(max = 15)
  private String phoneNumber;

  @NotBlank(message = "email is required")
  @Email(message = "Invalid email format")
  @Size(max = 50)
  private String email;

  @Size(max = 50)
  private String firstName;

  @Size(max = 50)
  private String lastName;

  @Size(max = 255)
  private String address;
}
