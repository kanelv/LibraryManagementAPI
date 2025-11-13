package com.kane.librarymanagement.config;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
  private String issuer;
  private String secretKey;
  private String accessToken;
  private long expirationTime;
}
