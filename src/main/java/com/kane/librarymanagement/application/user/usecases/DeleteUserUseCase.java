package com.kane.librarymanagement.application.user.usecases;

import com.kane.librarymanagement.domain.user.User;
import com.kane.librarymanagement.domain.user.UserId;
import com.kane.librarymanagement.domain.user.UserRepository;
import com.kane.librarymanagement.domain.shared.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteUserUseCase {
  private final UserRepository userRepository;

  @Transactional
  public void execute(Long id) {
    UserId userId = UserId.of(id);

    // Check if user exists
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException("User with ID " + id + " not found"));

    // Check if user has borrowed books - uses domain business logic
    if (user.hasBorrowedBooks()) {
      throw new BusinessException(
          "Cannot delete user with ID " + id + " because they have " +
          user.getBorrowedBooksCount() + " borrowed book(s)"
      );
    }

    // Delete the user
    userRepository.deleteById(userId);
  }
}
