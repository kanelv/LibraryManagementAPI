package com.kane.librarymanagement.infrastructure.persistence.jpa.repositories;

import com.kane.librarymanagement.infrastructure.persistence.jpa.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA repository for User persistence
 * Works with JPA entities only
 */
public interface UserJpaRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);
  Optional<User> findByEmail(String email);
  boolean existsByUsername(String username);
  boolean existsByEmail(String email);
}
