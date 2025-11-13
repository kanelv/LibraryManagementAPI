package com.kane.librarymanagement.domain.user;

import com.kane.librarymanagement.domain.shared.exceptions.BusinessException;
import com.kane.librarymanagement.domain.book.BookId;
import lombok.Getter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Domain Entity representing a User/Member in the library system
 */
@Getter
public class User {
  // Getters
  @Getter
  private UserId id;
  @Getter
  private String username;
  @Getter
  private String phoneNumber;
  @Getter
  private Email email;
  @Getter
  private Role role;
  @Getter
  private String password;
  @Getter
  private String firstName;
  @Getter
  private String lastName;
  @Getter
  private String address;
  @Getter
  private Boolean active;
  @Getter
  private LocalDate membershipDate;
  @Getter
  private OffsetDateTime createdAt;
  @Getter
  private OffsetDateTime updatedAt;
  @Getter
  private List<BookId> borrowedBookIds;

  private User() {
    this.borrowedBookIds = new ArrayList<>();
  }

  public static Builder builder() {
    return new Builder();
  }

  // Business logic methods
  public boolean isActive() {
    return active != null && active;
  }

  public void activate() {
    this.active = true;
  }

  public void deactivate() {
    if (hasBorrowedBooks()) {
      throw new BusinessException("Cannot deactivate user with borrowed books");
    }
    this.active = false;
  }

  public void borrowBook(BookId bookId) {
    if (!isActive()) {
      throw new BusinessException("Cannot borrow books: user account is inactive");
    }

    if (!role.canBorrow(getBorrowedBooksCount())) {
      throw new BusinessException(
          "Cannot borrow: role limit of " + role.maxBookNumber() + " books reached"
      );
    }

    if (borrowedBookIds.contains(bookId)) {
      throw new BusinessException("User has already borrowed this book");
    }
    borrowedBookIds.add(bookId);
  }

  public void returnBook(BookId bookId) {
    if (!borrowedBookIds.contains(bookId)) {
      throw new BusinessException("User has not borrowed this book");
    }
    borrowedBookIds.remove(bookId);
  }

  public boolean hasBorrowedBooks() {
    return !borrowedBookIds.isEmpty();
  }

  public int getBorrowedBooksCount() {
    return borrowedBookIds.size();
  }

  public void updateProfile(String phoneNumber, Email email, String firstName,
                           String lastName, String address) {
    this.phoneNumber = phoneNumber;
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.address = address;
  }

  public String getFullName() {
    if (firstName != null && lastName != null) {
      return firstName + " " + lastName;
    }
    return username;
  }

  public List<BookId> getBorrowedBookIds() {
    return new ArrayList<>(borrowedBookIds);
  }

  // Builder
  public static class Builder {
    private final User user = new User();

    public Builder id(UserId id) {
      user.id = id;
      return this;
    }

    public Builder username(String username) {
      user.username = username;
      return this;
    }

    public Builder phoneNumber(String phoneNumber) {
      user.phoneNumber = phoneNumber;
      return this;
    }

    public Builder email(Email email) {
      user.email = email;
      return this;
    }

    public Builder role(Role role) {
      user.role = role;
      return this;
    }

    public Builder password(String password) {
      user.password = password;
      return this;
    }

    public Builder firstName(String firstName) {
      user.firstName = firstName;
      return this;
    }

    public Builder lastName(String lastName) {
      user.lastName = lastName;
      return this;
    }

    public Builder address(String address) {
      user.address = address;
      return this;
    }

    public Builder active(Boolean active) {
      user.active = active;
      return this;
    }

    public Builder membershipDate(LocalDate membershipDate) {
      user.membershipDate = membershipDate;
      return this;
    }

    public Builder createdAt(OffsetDateTime createdAt) {
      user.createdAt = createdAt;
      return this;
    }

    public Builder updatedAt(OffsetDateTime updatedAt) {
      user.updatedAt = updatedAt;
      return this;
    }

    public Builder borrowedBookIds(List<BookId> borrowedBookIds) {
      user.borrowedBookIds = new ArrayList<>(borrowedBookIds);
      return this;
    }

    public User build() {
      if (user.username == null || user.username.isBlank()) {
        throw new BusinessException("Username is required");
      }
      if (user.email == null) {
        throw new BusinessException("Email is required");
      }
      if (user.password == null || user.password.isBlank()) {
        throw new BusinessException("Password is required");
      }
      if (user.active == null) {
        user.active = true;
      }
      if (user.membershipDate == null) {
        user.membershipDate = LocalDate.now();
      }
      return user;
    }
  }
}
