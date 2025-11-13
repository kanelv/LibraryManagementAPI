package com.kane.librarymanagement.application.user.usecases;

import com.kane.librarymanagement.domain.user.User;
import com.kane.librarymanagement.domain.user.UserId;
import com.kane.librarymanagement.domain.user.UserRepository;
import com.kane.librarymanagement.domain.user.Email;
import com.kane.librarymanagement.application.user.dto.UpdateUserRequest;
import com.kane.librarymanagement.application.user.dto.UserResponse;
import com.kane.librarymanagement.domain.shared.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UpdateUserUseCase {
  private final UserRepository userRepository;

  @Transactional
  public UserResponse execute(Long id, UpdateUserRequest request) {
    // Find existing user
    User user = userRepository.findById(UserId.of(id))
        .orElseThrow(() -> new BusinessException("User with ID " + id + " not found"));

    Email newEmail = Email.of(request.getEmail());

    // Validate email uniqueness if email is being changed
    if (!user.getEmail().equals(newEmail)) {
      Optional<User> existingUser = userRepository.findByEmail(newEmail);
      if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId())) {
        throw new BusinessException("Email " + request.getEmail() + " is already in use");
      }
    }

    // Use domain business logic to update profile
    user.updateProfile(
        request.getPhoneNumber(),
        newEmail,
        request.getFirstName(),
        request.getLastName(),
        request.getAddress()
    );

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
        .active(saved.getActive())
        .membershipDate(saved.getMembershipDate())
        .createdAt(saved.getCreatedAt())
        .build();
  }
}
