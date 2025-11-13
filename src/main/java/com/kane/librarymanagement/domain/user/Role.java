package com.kane.librarymanagement.domain.user;

import com.kane.librarymanagement.domain.enums.RoleType;
import java.util.Objects;

/**
 * Value Object representing a User's Role with its permissions and limits
 * Immutable - changes require creating a new Role instance
 */
public record Role(
    RoleType roleType,
    int maxBookNumber,
    int maxBorrowDuration
) {
  public Role {
    Objects.requireNonNull(roleType, "Role type cannot be null");
    if (maxBookNumber < 0) {
      throw new IllegalArgumentException("Max book number must be >= 0");
    }
    if (maxBorrowDuration < 1) {
      throw new IllegalArgumentException("Max borrow duration must be > 0");
    }
  }

  public static Role admin() {
    return new Role(RoleType.ADMIN, 20, 60);
  }

  public static Role user() {
    return new Role(RoleType.USER, 5, 14);
  }

  public static Role guest() {
    return new Role(RoleType.GUEST, 2, 7);
  }

  public static Role fromType(RoleType roleType) {
    return switch (roleType) {
      case ADMIN -> admin();
      case USER -> user();
      case GUEST -> guest();
    };
  }

  public boolean canBorrow(int currentBorrowedBooks) {
    return currentBorrowedBooks < maxBookNumber;
  }
}
