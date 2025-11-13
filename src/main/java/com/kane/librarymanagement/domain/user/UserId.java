package com.kane.librarymanagement.domain.user;

import java.util.Objects;

/**
 * Value Object representing a User's unique identifier
 */
public record UserId(Long value) {
  public UserId {
    Objects.requireNonNull(value, "User ID cannot be null");
  }

  public static UserId of(Long value) {
    return new UserId(value);
  }
}
