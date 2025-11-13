package com.kane.librarymanagement.infrastructure.persistence.jpa.mappers;

import com.kane.librarymanagement.domain.user.Role;
import com.kane.librarymanagement.domain.user.User;
import com.kane.librarymanagement.domain.user.UserId;
import com.kane.librarymanagement.domain.user.Email;
import com.kane.librarymanagement.domain.book.BookId;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Mapper to convert between JPA User entity and Domain User entity
 */
@Component
public class UserMapper {

  /**
   * Converts JPA User entity to Domain User entity
   */
  public User toDomain(com.kane.librarymanagement.infrastructure.persistence.jpa.entities.User jpaUser) {
    if (jpaUser == null) {
      return null;
    }

    return User.builder()
        .id(jpaUser.getId() != null ? UserId.of(jpaUser.getId()) : null)
        .username(jpaUser.getUsername())
        .phoneNumber(jpaUser.getPhoneNumber())
        .email(Email.of(jpaUser.getEmail()))
        .role(new Role(
            jpaUser.getRoleType(),
            jpaUser.getMaxBookNumber(),
            jpaUser.getMaxBorrowDuration()
        ))
        .password(jpaUser.getPassword())
        .firstName(jpaUser.getFirstName())
        .lastName(jpaUser.getLastName())
        .address(jpaUser.getAddress())
        .active(jpaUser.getActive())
        .membershipDate(jpaUser.getMembershipDate())
        .createdAt(jpaUser.getCreatedAt())
        .updatedAt(jpaUser.getUpdatedAt())
        .borrowedBookIds(jpaUser.getBorrowedBooks() != null ?
            jpaUser.getBorrowedBooks().stream()
                .map(book -> BookId.of(book.getId()))
                .collect(Collectors.toList()) :
            new ArrayList<>())
        .build();
  }

  /**
   * Converts Domain User entity to JPA User entity
   */
  public com.kane.librarymanagement.infrastructure.persistence.jpa.entities.User toJpa(User domainUser) {
    if (domainUser == null) {
      return null;
    }

    return com.kane.librarymanagement.infrastructure.persistence.jpa.entities.User.builder()
        .id(domainUser.getId() != null ? domainUser.getId().value() : null)
        .username(domainUser.getUsername())
        .phoneNumber(domainUser.getPhoneNumber())
        .email(domainUser.getEmail().value())
        .roleType(domainUser.getRole() != null ? domainUser.getRole().roleType() : null)
        .maxBookNumber(domainUser.getRole() != null ? domainUser.getRole().maxBookNumber() : null)
        .maxBorrowDuration(domainUser.getRole() != null ? domainUser.getRole().maxBorrowDuration() : null)
        .password(domainUser.getPassword())
        .firstName(domainUser.getFirstName())
        .lastName(domainUser.getLastName())
        .address(domainUser.getAddress())
        .active(domainUser.getActive())
        .membershipDate(domainUser.getMembershipDate())
        .createdAt(domainUser.getCreatedAt())
        .updatedAt(domainUser.getUpdatedAt())
        .build();
  }

  /**
   * Updates JPA entity with values from Domain entity (for update operations)
   */
  public void updateJpaFromDomain(com.kane.librarymanagement.infrastructure.persistence.jpa.entities.User jpaUser, User domainUser) {
    if (jpaUser == null || domainUser == null) {
      return;
    }

    jpaUser.setUsername(domainUser.getUsername());
    jpaUser.setPhoneNumber(domainUser.getPhoneNumber());
    jpaUser.setEmail(domainUser.getEmail().value());

    if (domainUser.getRole() != null) {
      jpaUser.setRoleType(domainUser.getRole().roleType());
      jpaUser.setMaxBookNumber(domainUser.getRole().maxBookNumber());
      jpaUser.setMaxBorrowDuration(domainUser.getRole().maxBorrowDuration());
    }

    jpaUser.setPassword(domainUser.getPassword());
    jpaUser.setFirstName(domainUser.getFirstName());
    jpaUser.setLastName(domainUser.getLastName());
    jpaUser.setAddress(domainUser.getAddress());
    jpaUser.setActive(domainUser.getActive());
    jpaUser.setMembershipDate(domainUser.getMembershipDate());
  }
}
