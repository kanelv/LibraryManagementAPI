package com.kane.librarymanagement.domain.user;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User domain entities
 * This works with domain entities, not JPA entities
 */
public interface UserRepository {
  User save(User user);
  Optional<User> findById(UserId id);
  Optional<User> findByUsername(String username);
  Optional<User> findByEmail(Email email);
  List<User> findAll();
  void deleteById(UserId id);
  boolean existsByUsername(String username);
  boolean existsByEmail(String email);
}
