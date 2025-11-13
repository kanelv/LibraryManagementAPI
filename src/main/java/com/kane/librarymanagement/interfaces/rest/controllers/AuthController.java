package com.kane.librarymanagement.interfaces.rest.controllers;

import com.kane.librarymanagement.application.auth.usecases.SignInUseCase;
import com.kane.librarymanagement.application.auth.dto.SignInRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
  private final SignInUseCase signInUseCase;

  @PostMapping("/sign-in")
  public ResponseEntity<?> signIn(
      @Valid @RequestBody SignInRequest request,
      HttpServletResponse response) {

    Optional<String> token = signInUseCase.run(request.getUsername(), request.getPassword());

    if (token.isPresent()) {
      Cookie jwtCookie = new Cookie("jwt", token.get());
      jwtCookie.setHttpOnly(true);
      jwtCookie.setSecure(true);
      jwtCookie.setPath("/");
      jwtCookie.setMaxAge(86400); // 1 day
      response.addCookie(jwtCookie);

      return ResponseEntity.ok(Map.of("message", "Sign-in successful"));
    } else {
      return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
    }
  }

  @PostMapping("/sign-out")
  public ResponseEntity<?> signOut(HttpServletResponse response) {
    // Clear the JWT cookie by setting MaxAge to 0
    Cookie jwtCookie = new Cookie("jwt", null);
    jwtCookie.setHttpOnly(true);
    jwtCookie.setSecure(true);
    jwtCookie.setPath("/");
    jwtCookie.setMaxAge(0); // Delete the cookie
    response.addCookie(jwtCookie);

    return ResponseEntity.ok(Map.of("message", "Sign-out successful"));
  }

}
