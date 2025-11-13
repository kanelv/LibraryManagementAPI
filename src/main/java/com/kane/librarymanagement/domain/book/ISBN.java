package com.kane.librarymanagement.domain.book;

import com.kane.librarymanagement.domain.shared.exceptions.BusinessException;

import java.util.Objects;

/**
 * Value Object representing an ISBN (International Standard Book Number)
 */
public record ISBN(String value) {
  public ISBN {
    Objects.requireNonNull(value, "ISBN cannot be null");
    if (!isValid(value)) {
      throw new BusinessException("Invalid ISBN format: " + value);
    }
  }

  private static boolean isValid(String isbn) {
    if (isbn == null || isbn.isBlank()) {
      return false;
    }
    // Supports ISBN-10, ISBN-13, or ISBN with hyphens
    return isbn.matches("^\\d{10}$|^\\d{13}$|^\\d{3}-\\d{1}-\\d{2}-\\d{6}-\\d{1}$");
  }

  public static ISBN of(String value) {
    return new ISBN(value);
  }
}
