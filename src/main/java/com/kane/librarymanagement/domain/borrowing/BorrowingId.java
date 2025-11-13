package com.kane.librarymanagement.domain.borrowing;

import java.util.Objects;

/**
 * Value Object representing a Borrowing's unique identifier
 */
public record BorrowingId(Long value) {
  public BorrowingId {
    Objects.requireNonNull(value, "Borrowing ID cannot be null");
  }

  public static BorrowingId of(Long value) {
    return new BorrowingId(value);
  }
}
