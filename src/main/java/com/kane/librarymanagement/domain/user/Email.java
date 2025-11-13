package com.kane.librarymanagement.domain.user;

import com.kane.librarymanagement.domain.shared.exceptions.BusinessException;

import java.util.Objects;

/**
 * Value Object representing an Email address
 */
public record Email(String value) {
  public Email {
    Objects.requireNonNull(value, "Email cannot be null");
    if (!isValid(value)) {
      throw new BusinessException("Invalid email format: " + value);
    }
  }

  private static boolean isValid(String email) {
    if (email == null || email.isBlank()) {
      return false;
    }
    // Basic email validation
    return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
  }

  public static Email of(String value) {
    return new Email(value);
  }
}
