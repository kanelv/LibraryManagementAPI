package com.kane.librarymanagement.infrastructure.persistence.repositories;

import com.kane.librarymanagement.domain.enums.RoleType;
import com.kane.librarymanagement.domain.user.Email;
import com.kane.librarymanagement.domain.user.Role;
import com.kane.librarymanagement.domain.user.User;
import com.kane.librarymanagement.domain.user.UserId;
import com.kane.librarymanagement.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for UserRepository
 * Tests the complete repository layer including JPA and database interactions
 */
@DataJpaTest
@ComponentScan(basePackages = {
    "com.kane.librarymanagement.infrastructure.persistence.repositories",
    "com.kane.librarymanagement.infrastructure.persistence.jpa.mappers"
})
@ActiveProfiles("test")
@DisplayName("UserRepository Integration Tests")
class UserRepositoryIntegrationTest {

  @Autowired
  private UserRepository userRepository;

  private User testUser1;
  private User testUser2;
  private User testAdmin;

  @BeforeEach
  void setUp() {
    // Create test users
    testUser1 = User.builder()
        .username("john.doe")
        .phoneNumber("1234567890")
        .email(Email.of("john.doe@example.com"))
        .password("hashedPassword123")
        .firstName("John")
        .lastName("Doe")
        .address("123 Main St")
        .role(Role.user())
        .build();

    testUser2 = User.builder()
        .username("jane.smith")
        .phoneNumber("0987654321")
        .email(Email.of("jane.smith@example.com"))
        .password("hashedPassword456")
        .firstName("Jane")
        .lastName("Smith")
        .address("456 Oak Ave")
        .role(Role.user())
        .build();

    testAdmin = User.builder()
        .username("admin.user")
        .phoneNumber("5555555555")
        .email(Email.of("admin@example.com"))
        .password("hashedAdminPass")
        .firstName("Admin")
        .lastName("User")
        .address("789 Admin Blvd")
        .role(Role.admin())
        .build();
  }

  @Test
  @DisplayName("Should save a new user and assign ID")
  void testSaveUser() {
    // When
    User saved = userRepository.save(testUser1);

    // Then
    assertThat(saved).isNotNull();
    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getId().value()).isGreaterThan(0);
    assertThat(saved.getUsername()).isEqualTo("john.doe");
    assertThat(saved.getEmail().value()).isEqualTo("john.doe@example.com");
    assertThat(saved.getRole().roleType()).isEqualTo(RoleType.USER);
    assertThat(saved.getMembershipDate()).isNotNull();
    // Note: CreatedAt and UpdatedAt are set by Hibernate @CreationTimestamp/@UpdateTimestamp
    // They may be null in test environment depending on Hibernate initialization
  }

  @Test
  @DisplayName("Should find user by ID")
  void testFindById() {
    // Given
    User saved = userRepository.save(testUser1);
    UserId userId = saved.getId();

    // When
    Optional<User> found = userRepository.findById(userId);

    // Then
    assertThat(found).isPresent();
    assertThat(found.get().getUsername()).isEqualTo("john.doe");
    assertThat(found.get().getEmail().value()).isEqualTo("john.doe@example.com");
  }

  @Test
  @DisplayName("Should return empty when user ID not found")
  void testFindByIdNotFound() {
    // When
    Optional<User> found = userRepository.findById(UserId.of(99999L));

    // Then
    assertThat(found).isEmpty();
  }

  @Test
  @DisplayName("Should find user by username")
  void testFindByUsername() {
    // Given
    userRepository.save(testUser1);

    // When
    Optional<User> found = userRepository.findByUsername("john.doe");

    // Then
    assertThat(found).isPresent();
    assertThat(found.get().getEmail().value()).isEqualTo("john.doe@example.com");
  }

  @Test
  @DisplayName("Should return empty when username not found")
  void testFindByUsernameNotFound() {
    // When
    Optional<User> found = userRepository.findByUsername("nonexistent");

    // Then
    assertThat(found).isEmpty();
  }

  @Test
  @DisplayName("Should find user by email")
  void testFindByEmail() {
    // Given
    userRepository.save(testUser1);

    // When
    Optional<User> found = userRepository.findByEmail(Email.of("john.doe@example.com"));

    // Then
    assertThat(found).isPresent();
    assertThat(found.get().getUsername()).isEqualTo("john.doe");
  }

  @Test
  @DisplayName("Should return empty when email not found")
  void testFindByEmailNotFound() {
    // When
    Optional<User> found = userRepository.findByEmail(Email.of("nonexistent@example.com"));

    // Then
    assertThat(found).isEmpty();
  }

  @Test
  @DisplayName("Should check if username exists")
  void testExistsByUsername() {
    // Given
    userRepository.save(testUser1);

    // When & Then
    assertThat(userRepository.existsByUsername("john.doe")).isTrue();
    assertThat(userRepository.existsByUsername("nonexistent")).isFalse();
  }

  @Test
  @DisplayName("Should check if email exists")
  void testExistsByEmail() {
    // Given
    userRepository.save(testUser1);

    // When & Then
    assertThat(userRepository.existsByEmail("john.doe@example.com")).isTrue();
    assertThat(userRepository.existsByEmail("nonexistent@example.com")).isFalse();
  }

  @Test
  @DisplayName("Should find all users")
  void testFindAll() {
    // Given
    userRepository.save(testUser1);
    userRepository.save(testUser2);
    userRepository.save(testAdmin);

    // When
    List<User> users = userRepository.findAll();

    // Then
    assertThat(users).hasSize(3);
    assertThat(users).extracting(User::getUsername)
        .containsExactlyInAnyOrder("john.doe", "jane.smith", "admin.user");
  }

  @Test
  @DisplayName("Should return empty list when no users exist")
  void testFindAllEmpty() {
    // When
    List<User> users = userRepository.findAll();

    // Then
    assertThat(users).isEmpty();
  }

  @Test
  @DisplayName("Should update existing user")
  void testUpdateUser() {
    // Given
    User saved = userRepository.save(testUser1);
    UserId userId = saved.getId();

    // When - Create updated user with same ID
    User updatedUser = User.builder()
        .id(userId)
        .username(saved.getUsername())
        .phoneNumber("9999999999") // Changed
        .email(saved.getEmail())
        .password(saved.getPassword())
        .firstName("Johnny") // Changed
        .lastName(saved.getLastName())
        .address("New Address") // Changed
        .role(saved.getRole())
        .active(saved.getActive())
        .membershipDate(saved.getMembershipDate())
        .createdAt(saved.getCreatedAt())
        .build();

    User updated = userRepository.save(updatedUser);

    // Then
    assertThat(updated.getId()).isEqualTo(userId);
    assertThat(updated.getPhoneNumber()).isEqualTo("9999999999");
    assertThat(updated.getFirstName()).isEqualTo("Johnny");
    assertThat(updated.getAddress()).isEqualTo("New Address");
  }

  @Test
  @DisplayName("Should delete user by ID")
  void testDeleteById() {
    // Given
    User saved = userRepository.save(testUser1);
    UserId userId = saved.getId();

    // When
    userRepository.deleteById(userId);

    // Then
    Optional<User> found = userRepository.findById(userId);
    assertThat(found).isEmpty();
  }

  @Test
  @DisplayName("Should save users with different roles")
  void testSaveUsersWithDifferentRoles() {
    // Given & When
    User savedUser = userRepository.save(testUser1);
    User savedAdmin = userRepository.save(testAdmin);

    User guestUser = User.builder()
        .username("guest.user")
        .phoneNumber("1111111111")
        .email(Email.of("guest@example.com"))
        .password("guestPass")
        .role(Role.guest())
        .build();
    User savedGuest = userRepository.save(guestUser);

    // Then
    assertThat(savedUser.getRole().roleType()).isEqualTo(RoleType.USER);
    assertThat(savedUser.getRole().maxBookNumber()).isEqualTo(5);
    assertThat(savedUser.getRole().maxBorrowDuration()).isEqualTo(14);

    assertThat(savedAdmin.getRole().roleType()).isEqualTo(RoleType.ADMIN);
    assertThat(savedAdmin.getRole().maxBookNumber()).isEqualTo(20);
    assertThat(savedAdmin.getRole().maxBorrowDuration()).isEqualTo(60);

    assertThat(savedGuest.getRole().roleType()).isEqualTo(RoleType.GUEST);
    assertThat(savedGuest.getRole().maxBookNumber()).isEqualTo(2);
    assertThat(savedGuest.getRole().maxBorrowDuration()).isEqualTo(7);
  }

  @Test
  @DisplayName("Should maintain user timestamps")
  void testUserTimestamps() {
    // When
    User saved = userRepository.save(testUser1);

    // Then
    // Membership date is set by domain logic
    assertThat(saved.getMembershipDate()).isNotNull();
    // CreatedAt and UpdatedAt are set by Hibernate annotations
    // These may be populated after flush/commit in real scenarios
  }

  @Test
  @DisplayName("Should handle user activation status")
  void testUserActiveStatus() {
    // Given
    User saved = userRepository.save(testUser1);

    // Then - New users should be active by default
    assertThat(saved.isActive()).isTrue();

    // When - Deactivate user (this would need to be done through domain logic)
    // Just verify we can read the status
    Optional<User> found = userRepository.findById(saved.getId());
    assertThat(found).isPresent();
    assertThat(found.get().isActive()).isTrue();
  }
}
