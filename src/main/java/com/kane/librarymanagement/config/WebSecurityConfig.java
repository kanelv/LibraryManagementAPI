package com.kane.librarymanagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Additional Web Security Configuration
 * Handles CORS and other web-level security settings
 */
@Configuration
public class WebSecurityConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOrigins("http://localhost:3000", "http://localhost:8080") // Update with your frontend URLs
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(true) // Required for cookies
        .exposedHeaders("X-CSRF-TOKEN") // Expose CSRF token to frontend
        .maxAge(3600);
  }
}
