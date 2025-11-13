package com.kane.librarymanagement.domain.book;

import com.kane.librarymanagement.domain.shared.exceptions.BusinessException;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain Entity representing a Book in the library
 * This is a rich domain model with business logic
 */
@Getter
public class Book {
  // Getters
  private BookId id;
  private ISBN isbn;
  private String title;
  private String author;
  private String publisher;
  private Integer publishedYear;
  private String genre;
  private BigDecimal price;
  private Integer totalCopies;
  private Integer availableCopies;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  // Private constructor - use builder
  private Book() {
  }

  public static Builder builder() {
    return new Builder();
  }

  // Business logic methods
  public boolean isAvailable() {
    return availableCopies != null && availableCopies > 0;
  }

  public void borrowCopy() {
    if (!isAvailable()) {
      throw new BusinessException("No copies of '" + title + "' are currently available");
    }
    this.availableCopies--;
  }

  public void returnCopy() {
    if (availableCopies >= totalCopies) {
      throw new BusinessException("All copies of '" + title + "' are already returned");
    }
    this.availableCopies++;
  }

  public void updateDetails(String title, String author, String publisher,
                           Integer publishedYear, String genre) {
    if (title == null || title.isBlank()) {
      throw new BusinessException("Book title cannot be empty");
    }
    if (author == null || author.isBlank()) {
      throw new BusinessException("Book author cannot be empty");
    }
    this.title = title;
    this.author = author;
    this.publisher = publisher;
    this.publishedYear = publishedYear;
    this.genre = genre;
  }

  public void updateTotalCopies(Integer newTotalCopies) {
    int borrowedCopies = totalCopies - availableCopies;
    if (newTotalCopies < borrowedCopies) {
      throw new BusinessException(
          "Cannot reduce total copies to " + newTotalCopies +
          " because " + borrowedCopies + " copies are currently borrowed"
      );
    }
    this.availableCopies = newTotalCopies - borrowedCopies;
    this.totalCopies = newTotalCopies;
  }

  public boolean hasBorrowedCopies() {
    return availableCopies < totalCopies;
  }

  // Builder
  public static class Builder {
    private final Book book = new Book();

    public Builder id(BookId id) {
      book.id = id;
      return this;
    }

    public Builder isbn(ISBN isbn) {
      book.isbn = isbn;
      return this;
    }

    public Builder title(String title) {
      book.title = title;
      return this;
    }

    public Builder author(String author) {
      book.author = author;
      return this;
    }

    public Builder publisher(String publisher) {
      book.publisher = publisher;
      return this;
    }

    public Builder publishedYear(Integer publishedYear) {
      book.publishedYear = publishedYear;
      return this;
    }

    public Builder genre(String genre) {
      book.genre = genre;
      return this;
    }

    public Builder price(BigDecimal price) {
      book.price = price;
      return this;
    }

    public Builder totalCopies(Integer totalCopies) {
      book.totalCopies = totalCopies;
      return this;
    }

    public Builder availableCopies(Integer availableCopies) {
      book.availableCopies = availableCopies;
      return this;
    }

    public Builder createdAt(LocalDateTime createdAt) {
      book.createdAt = createdAt;
      return this;
    }

    public Builder updatedAt(LocalDateTime updatedAt) {
      book.updatedAt = updatedAt;
      return this;
    }

    public Book build() {
      if (book.isbn == null) {
        throw new BusinessException("ISBN is required");
      }
      if (book.title == null || book.title.isBlank()) {
        throw new BusinessException("Title is required");
      }
      if (book.author == null || book.author.isBlank()) {
        throw new BusinessException("Author is required");
      }
      if (book.totalCopies == null || book.totalCopies < 0) {
        throw new BusinessException("Total copies must be greater than or equal to 0");
      }
      if (book.availableCopies == null) {
        book.availableCopies = book.totalCopies;
      }
      return book;
    }
  }
}
