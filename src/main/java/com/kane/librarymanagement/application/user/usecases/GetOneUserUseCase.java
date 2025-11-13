package com.kane.librarymanagement.application.user.usecases;

import com.kane.librarymanagement.domain.user.User;
import com.kane.librarymanagement.domain.user.UserId;
import com.kane.librarymanagement.domain.user.UserRepository;
import com.kane.librarymanagement.application.user.dto.UserResponse;
import com.kane.librarymanagement.domain.shared.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetOneUserUseCase {
  private final UserRepository userRepository;

  @Transactional
  public UserResponse execute(Long id) {
    User user = userRepository.findById(UserId.of(id))
        .orElseThrow(() -> new BusinessException("User with ID " + id + " not found"));

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
