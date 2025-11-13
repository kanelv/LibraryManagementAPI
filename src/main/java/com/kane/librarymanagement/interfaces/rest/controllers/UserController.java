package com.kane.librarymanagement.interfaces.rest.controllers;

import com.kane.librarymanagement.application.user.usecases.*;
import com.kane.librarymanagement.application.user.dto.CreateUserRequest;
import com.kane.librarymanagement.application.user.dto.UpdateUserRequest;
import com.kane.librarymanagement.application.user.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final CreateUserUseCase createUserUseCase;
  private final ListUserUseCase listUserUseCase;
  private final GetOneUserUseCase getOneUserUseCase;
  private final UpdateUserUseCase updateUserUseCase;
  private final DeleteUserUseCase deleteUserUseCase;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserResponse> createUser(
      @Valid @RequestBody CreateUserRequest request) {
    UserResponse response = createUserUseCase.execute(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<UserResponse>> listUsers() {
    List<UserResponse> response = listUserUseCase.execute();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
  public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
    UserResponse response = getOneUserUseCase.execute(id);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
  public ResponseEntity<UserResponse> updateUser(
      @PathVariable Long id,
      @Valid @RequestBody UpdateUserRequest request) {
    UserResponse response = updateUserUseCase.execute(id, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    deleteUserUseCase.execute(id);
    return ResponseEntity.noContent().build();
  }
}
