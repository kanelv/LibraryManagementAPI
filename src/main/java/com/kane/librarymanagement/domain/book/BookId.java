package com.kane.librarymanagement.domain.book;

import java.util.Objects;

/**
 * Value Object representing a Book's unique identifier
 */
public record BookId(Long value) {
  public BookId {
    Objects.requireNonNull(value, "Book ID cannot be null");
  }

  public static BookId of(Long value) {
    return new BookId(value);
  }
}
