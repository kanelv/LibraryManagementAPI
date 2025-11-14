package com.kane.librarymanagement.infrastructure.persistence.repositories;

import com.kane.librarymanagement.domain.user.User;
import com.kane.librarymanagement.domain.user.UserId;
import com.kane.librarymanagement.domain.user.UserRepository;
import com.kane.librarymanagement.domain.user.Email;
import com.kane.librarymanagement.infrastructure.persistence.jpa.mappers.UserMapper;
import com.kane.librarymanagement.infrastructure.persistence.jpa.repositories.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of UserRepository that bridges domain layer and persistence layer
 * This is an adapter that implements the domain repository interface
 * Uses JPA repository for persistence and mapper for conversions
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

  private final UserJpaRepository jpaRepository;
  private final UserMapper mapper;

  @Override
  public User save(User user) {
    com.kane.librarymanagement.infrastructure.persistence.jpa.entities.User jpaUser;

    if (user.getId() != null) {
      // Update existing user
      jpaUser = jpaRepository.findById(user.getId().value())
          .orElseThrow(() -> new RuntimeException("User not found with id: " + user.getId().value()));
      mapper.updateJpaFromDomain(jpaUser, user);
    } else {
      // Create new user
      jpaUser = mapper.toJpa(user);
    }

    com.kane.librarymanagement.infrastructure.persistence.jpa.entities.User savedJpaUser = jpaRepository.save(jpaUser);
    return mapper.toDomain(savedJpaUser);
  }

  @Override
  public Optional<User> findById(UserId id) {
    return jpaRepository.findById(id.value())
        .map(mapper::toDomain);
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return jpaRepository.findByUsername(username)
        .map(mapper::toDomain);
  }

  @Override
  public Optional<User> findByEmail(Email email) {
    return jpaRepository.findByEmail(email.value())
        .map(mapper::toDomain);
  }

  @Override
  public List<User> findAll() {
    return jpaRepository.findAll().stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public void deleteById(UserId id) {
    jpaRepository.deleteById(id.value());
  }

  @Override
  public boolean existsByUsername(String username) {
    return jpaRepository.existsByUsername(username);
  }

  @Override
  public boolean existsByEmail(String email) {
    return jpaRepository.existsByEmail(email);
  }
}
