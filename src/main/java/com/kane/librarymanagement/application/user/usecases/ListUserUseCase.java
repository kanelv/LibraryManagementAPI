package com.kane.librarymanagement.application.user.usecases;

import com.kane.librarymanagement.domain.user.User;
import com.kane.librarymanagement.domain.user.UserRepository;
import com.kane.librarymanagement.application.user.dto.UserResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ListUserUseCase {
  private final UserRepository userRepository;

  @Transactional
  public List<UserResponse> execute() {
    List<User> users = userRepository.findAll();

    return users.stream().map(user ->
        UserResponse.builder()
            .id(user.getId().value())
            .username(user.getUsername())
            .phoneNumber(user.getPhoneNumber())
            .email(user.getEmail().value())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .address(user.getAddress())
            .membershipDate(user.getMembershipDate())
            .createdAt(user.getCreatedAt())
            .build()
    ).toList();
  }
}
