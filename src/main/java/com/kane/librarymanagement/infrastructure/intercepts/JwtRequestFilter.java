package com.kane.librarymanagement.infrastructure.intercepts;

import com.kane.librarymanagement.application.common.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

  // Public endpoints that don't require authentication
  private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
      "/auth/sign-in",
      "/auth/register",
      "/auth/csrf"
  );

  private final JwtService jwtService;

  @Autowired
  public JwtRequestFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    String requestPath = request.getRequestURI();

    // Skip JWT validation for public endpoints
    if (isPublicEndpoint(requestPath)) {
      chain.doFilter(request, response);
      return;
    }

    try {
      // Extract JWT token from cookie
      String token = extractJwtFromCookie(request);

      if (token != null && jwtService.validateToken(token)) {
        // Token is valid - set authentication in SecurityContext
        setAuthentication(token);
        logger.debug("JWT authentication successful for user: {}", jwtService.extractUsername(token));
        chain.doFilter(request, response);
      } else {
        // Token is missing or invalid - return 401 Unauthorized
        handleUnauthorized(response, "Invalid or missing JWT token");
      }

    } catch (Exception e) {
      // Any exception during token processing - return 401
      logger.error("JWT authentication error: {}", e.getMessage());
      handleUnauthorized(response, "Authentication failed: " + e.getMessage());
    }
  }

  /**
   * Extracts JWT token from the "jwt" cookie
   */
  private String extractJwtFromCookie(HttpServletRequest request) {
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if ("jwt".equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }

  /**
   * Sets authentication in SecurityContext with user details and authorities
   */
  private void setAuthentication(String token) {
    String username = jwtService.extractUsername(token);
    String role = jwtService.extractRole(token);

    // Create authority with ROLE_ prefix (Spring Security convention)
    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

    // Create UserDetails with username and authorities
    UserDetails userDetails = User.builder()
        .username(username)
        .password("") // Password not needed for JWT authentication
        .authorities(authority)
        .build();

    // Create authentication token
    UsernamePasswordAuthenticationToken authToken =
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

    // Set authentication in SecurityContext
    SecurityContextHolder.getContext().setAuthentication(authToken);
  }

  /**
   * Checks if the request path is a public endpoint
   */
  private boolean isPublicEndpoint(String requestPath) {
    return PUBLIC_ENDPOINTS.stream().anyMatch(requestPath::startsWith);
  }

  /**
   * Sends 401 Unauthorized response with JSON error message
   */
  private void handleUnauthorized(HttpServletResponse response, String message) throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    String jsonResponse = String.format(
        "{\"error\": \"Unauthorized\", \"message\": \"%s\", \"status\": 401}",
        message
    );

    response.getWriter().write(jsonResponse);
    response.getWriter().flush();
  }
}
