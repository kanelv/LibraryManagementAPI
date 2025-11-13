package com.kane.librarymanagement.application.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
  private Long id;
  private String username;
  private String phoneNumber;
  private String email;
  private String firstName;
  private String lastName;
  private String address;
  private Boolean active;
  private LocalDate membershipDate;
  private OffsetDateTime createdAt;
}
