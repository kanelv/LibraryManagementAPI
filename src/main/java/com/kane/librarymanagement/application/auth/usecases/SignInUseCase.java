package com.kane.librarymanagement.application.auth.usecases;

import com.kane.librarymanagement.application.common.services.JwtService;
import com.kane.librarymanagement.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SignInUseCase {
  private final UserRepository userRepository;
  private final JwtService jwtService;
  private final PasswordEncoder passwordEncoder;

  public Optional<String> run(String username, String password) {
    return userRepository.findByUsername(username)
        .filter(user -> passwordEncoder.matches(password, user.getPassword()))
        .map(jwtService::generateToken);
  }
}
