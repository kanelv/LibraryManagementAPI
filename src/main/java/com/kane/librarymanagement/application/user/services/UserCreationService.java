package com.kane.librarymanagement.application.user.services;

import com.kane.librarymanagement.application.user.dto.UserResponse;
import com.kane.librarymanagement.domain.shared.exceptions.BusinessException;
import com.kane.librarymanagement.domain.user.Email;
import com.kane.librarymanagement.domain.user.Role;
import com.kane.librarymanagement.domain.user.User;
import com.kane.librarymanagement.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Shared service for user creation logic
 * Eliminates code duplication between RegisterUseCase and CreateUserUseCase
 */
@Service
@RequiredArgsConstructor
public class UserCreationService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  /**
   * Creates a new user with validation, password hashing, and role assignment
   *
   * @param username User's username
   * @param phoneNumber User's phone number
   * @param email User's email
   * @param password User's plain text password (will be hashed)
   * @param firstName User's first name (optional)
   * @param lastName User's last name (optional)
   * @param address User's address (optional)
   * @param role User's role
   * @return UserResponse with created user details
   * @throws BusinessException if username or email already exists
   */
  @Transactional
  public UserResponse createUser(
      String username,
      String phoneNumber,
      String email,
      String password,
      String firstName,
      String lastName,
      String address,
      Role role
  ) {
    // Validate username uniqueness
    if (userRepository.existsByUsername(username)) {
      throw new BusinessException("User with username " + username + " already exists");
    }

    // Validate email uniqueness
    if (userRepository.existsByEmail(email)) {
      throw new BusinessException("User with email " + email + " already exists");
    }

    // Hash the password
    String hashedPassword = passwordEncoder.encode(password);

    // Build domain entity
    User user = User.builder()
        .username(username)
        .phoneNumber(phoneNumber)
        .email(Email.of(email))
        .role(role)
        .password(hashedPassword)
        .firstName(firstName)
        .lastName(lastName)
        .address(address)
        .build();

    User saved = userRepository.save(user);

    // Map to response
    return mapToResponse(saved);
  }

  /**
   * Maps domain User entity to UserResponse DTO
   */
  private UserResponse mapToResponse(User user) {
    return UserResponse.builder()
        .id(user.getId().value())
        .username(user.getUsername())
        .phoneNumber(user.getPhoneNumber())
        .email(user.getEmail().value())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .address(user.getAddress())
        .membershipDate(user.getMembershipDate())
        .createdAt(user.getCreatedAt())
        .build();
  }
}
