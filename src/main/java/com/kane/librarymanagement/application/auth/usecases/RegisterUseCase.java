package com.kane.librarymanagement.application.auth.usecases;

import com.kane.librarymanagement.application.auth.dto.RegisterRequest;
import com.kane.librarymanagement.application.user.dto.UserResponse;
import com.kane.librarymanagement.application.user.services.UserCreationService;
import com.kane.librarymanagement.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for public user registration
 * Creates new user accounts with USER role by default
 */
@Service
@RequiredArgsConstructor
public class RegisterUseCase {
  private final UserCreationService userCreationService;

  @Transactional
  public UserResponse execute(RegisterRequest request) {
    // Delegate to shared service with USER role (public registration always gets USER role)
    return userCreationService.createUser(
        request.getUsername(),
        request.getPhoneNumber(),
        request.getEmail(),
        request.getPassword(),
        request.getFirstName(),
        request.getLastName(),
        request.getAddress(),
        Role.user()  // Always USER role for public registration
    );
  }
}
