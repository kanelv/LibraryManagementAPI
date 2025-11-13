package com.kane.librarymanagement.application.common.services;


import com.kane.librarymanagement.domain.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Validated
@Service
public class JwtService {
  private final long expirationTime;

  @Value("${jwt.issuer:Kane Inc.}")
  private final String issuer;

  @Value("${jwt.secret-key:this-is-a-very-long-and-secure-secret-key-123456}")
  private final String secretKey;
  private final SecretKey HMAC_SHA_KEY;

  public JwtService(
      @Value("${jwt.expiration-time:86400000}") @Min(1000) long expirationTime,
      @Value("${jwt.issuer:Kane Inc.}") String issuer,
      @Value("${jwt.secret-key:this-is-a-very-long-and-secure-secret-key-123456}") String secretKey
  ) {
    this.expirationTime = expirationTime;
    this.issuer = issuer;
    this.secretKey = secretKey;
    this.HMAC_SHA_KEY = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
  }

  private String createToken(Map<String, Object> claims, String username) {
    return Jwts.builder()
        .issuer(issuer)
        .claims(claims)
        .subject(username)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + expirationTime)) // 10 hours
        .signWith(HMAC_SHA_KEY).compact();
  }

  public String generateToken(User user) {
    Map<String, Object> claims = new HashMap<>();
    return createToken(claims, user.getUsername());
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().decryptWith(HMAC_SHA_KEY).build().parseSignedClaims(token).getPayload();
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  public Boolean validateToken(String token) {
    try {
      return !isTokenExpired(token);
    } catch (JwtException e) {
      return  false;
    }
  }
}

