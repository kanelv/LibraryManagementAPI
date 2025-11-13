package com.kane.librarymanagement.application.user.usecases;

import com.kane.librarymanagement.domain.user.User;
import com.kane.librarymanagement.domain.user.UserRepository;
import com.kane.librarymanagement.domain.user.Email;
import com.kane.librarymanagement.domain.user.Role;
import com.kane.librarymanagement.application.user.dto.CreateUserRequest;
import com.kane.librarymanagement.application.user.dto.UserResponse;
import com.kane.librarymanagement.domain.shared.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateUserUseCase {
  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder;

  @Transactional
  public UserResponse execute(CreateUserRequest request) {

    // Validate username uniqueness
    if (userRepository.existsByUsername(request.getUsername())) {
      throw new BusinessException("User with username " + request.getUsername() + " already exists");
    }

    // Hash the password
    String hashedPassword = passwordEncoder.encode(request.getPassword());

    // Build domain entity with default USER role
    User user = User.builder()
        .username(request.getUsername())
        .phoneNumber(request.getPhoneNumber())
        .email(Email.of(request.getEmail()))
        .role(Role.user())  // Default role for new users
        .password(hashedPassword)
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .address(request.getAddress())
        .build();

    User saved = userRepository.save(user);

    // Map to response
    return UserResponse.builder()
        .id(saved.getId().value())
        .username(saved.getUsername())
        .phoneNumber(saved.getPhoneNumber())
        .email(saved.getEmail().value())
        .firstName(saved.getFirstName())
        .lastName(saved.getLastName())
        .address(saved.getAddress())
        .membershipDate(saved.getMembershipDate())
        .createdAt(saved.getCreatedAt())
        .build();
  }
}
