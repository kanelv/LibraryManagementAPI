# Security Implementation Guide

This document describes the security measures implemented in the Library Management API and how to use them properly.

---

## Table of Contents

1. [JWT Authentication](#jwt-authentication)
2. [CSRF Protection](#csrf-protection)
3. [XSS Prevention](#xss-prevention)
4. [Authentication & Authorization](#authentication--authorization)
5. [Security Headers](#security-headers)
6. [Frontend Integration](#frontend-integration)
7. [Best Practices](#best-practices)

---

## JWT Authentication

### Overview

This application uses **JWT (JSON Web Token)** authentication with **HttpOnly cookies** for secure token storage.

### How It Works

1. **Login** → Server generates JWT with user info and role → Stores in HttpOnly cookie
2. **Subsequent Requests** → Browser automatically sends JWT cookie → Server validates and authorizes
3. **Logout** → Server clears the JWT cookie

### JWT Token Structure

The JWT contains the following claims:

```json
{
  "sub": "username",           // Subject (username)
  "role": "ADMIN",             // User role (ADMIN, USER, GUEST)
  "userId": 123,               // User ID
  "iss": "Kane Inc.",          // Issuer
  "iat": 1234567890,           // Issued at (timestamp)
  "exp": 1234654290            // Expiration (timestamp)
}
```

### Implementation Details

#### JwtService.java

**Token Generation (includes role):**
```java
public String generateToken(User user) {
  Map<String, Object> claims = new HashMap<>();
  claims.put("role", user.getRole().roleType().name());
  claims.put("userId", user.getId().value());
  return createToken(claims, user.getUsername());
}
```

**Token Verification:**
```java
public Boolean validateToken(String token) {
  try {
    return !isTokenExpired(token);
  } catch (JwtException e) {
    return false;
  }
}
```

#### JwtRequestFilter.java

The `JwtRequestFilter` runs on **every request** and:

1. **Skips public endpoints** (`/auth/sign-in`, `/auth/register`, `/auth/csrf`)
2. **Extracts JWT** from the `jwt` cookie
3. **Validates token** using `JwtService.validateToken()`
4. **Sets authentication** in `SecurityContext` with user's role
5. **Returns 401** if token is invalid/missing on protected endpoints

**Key Methods:**

```java
// Extract JWT from cookie
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

// Set authentication with role
private void setAuthentication(String token) {
  String username = jwtService.extractUsername(token);
  String role = jwtService.extractRole(token);

  SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
  UserDetails userDetails = User.builder()
      .username(username)
      .password("")
      .authorities(authority)
      .build();

  UsernamePasswordAuthenticationToken authToken =
      new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
  SecurityContextHolder.getContext().setAuthentication(authToken);
}

// Handle unauthorized access
private void handleUnauthorized(HttpServletResponse response, String message) {
  response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  response.setContentType("application/json");
  String jsonResponse = String.format(
    "{\"error\": \"Unauthorized\", \"message\": \"%s\", \"status\": 401}",
    message
  );
  response.getWriter().write(jsonResponse);
}
```

### Authentication Flow

```
┌─────────┐                 ┌─────────┐                 ┌──────────────┐
│ Client  │                 │ Server  │                 │ JWT Filter   │
└────┬────┘                 └────┬────┘                 └──────┬───────┘
     │                           │                             │
     │ POST /auth/sign-in        │                             │
     ├──────────────────────────>│                             │
     │                           │                             │
     │                           │ Validate credentials        │
     │                           │ Generate JWT                │
     │                           │ Set cookie: jwt=<token>     │
     │<──────────────────────────┤                             │
     │                           │                             │
     │ GET /api/users (with jwt) │                             │
     ├──────────────────────────>│──────────────────────────>│
     │                           │                             │
     │                           │                Extract JWT  │
     │                           │                Validate     │
     │                           │                Set Auth     │
     │                           │<────────────────────────────│
     │                           │                             │
     │                           │ Process request             │
     │<──────────────────────────┤                             │
     │                           │                             │
```

### 401 Unauthorized Response

When JWT is invalid or missing on protected endpoints:

**Response:**
```json
{
  "error": "Unauthorized",
  "message": "Invalid or missing JWT token",
  "status": 401
}
```

**HTTP Status:** `401 Unauthorized`

### Cookie Configuration

**AuthController.java:32-37**
```java
Cookie jwtCookie = new Cookie("jwt", token.get());
jwtCookie.setHttpOnly(true);  // JavaScript cannot access (prevents XSS)
jwtCookie.setSecure(true);     // HTTPS only in production
jwtCookie.setPath("/");        // Available for all paths
jwtCookie.setMaxAge(86400);    // 1 day (24 hours)
```

### Security Features

| Feature | Implementation | Purpose |
|---------|---------------|---------|
| **HttpOnly Cookie** | `setHttpOnly(true)` | Prevents XSS attacks (JavaScript cannot read) |
| **Secure Flag** | `setSecure(true)` | HTTPS only (production) |
| **Token Expiration** | 24 hours | Limits damage if token is compromised |
| **Role-Based Auth** | Role in JWT claims | Fine-grained access control |
| **Signature Verification** | HMAC-SHA256 | Prevents token tampering |

### Testing JWT Authentication

#### Test 1: Login and Get Token
```bash
curl -X POST http://localhost:8080/auth/sign-in \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  -c cookies.txt -v
```

#### Test 2: Access Protected Endpoint (Success)
```bash
curl -X GET http://localhost:8080/api/users \
  -b cookies.txt
```

#### Test 3: Access Without Token (401)
```bash
curl -X GET http://localhost:8080/api/users
# Response: {"error":"Unauthorized","message":"Invalid or missing JWT token","status":401}
```

#### Test 4: Access with Invalid Token (401)
```bash
curl -X GET http://localhost:8080/api/users \
  -H "Cookie: jwt=invalid-token-here"
# Response: {"error":"Unauthorized","message":"Invalid or missing JWT token","status":401}
```

---

## CSRF Protection

### What is CSRF?

**Cross-Site Request Forgery (CSRF)** is an attack that forces users to execute unwanted actions on a web application where they're authenticated. Since this app uses **cookie-based JWT authentication**, CSRF protection is critical.

### How It Works

1. **CSRF Token Storage**: Spring Security stores a CSRF token in a cookie named `XSRF-TOKEN`
2. **Token Transmission**: Frontend must read this cookie and include it in requests
3. **Validation**: Backend validates the token on state-changing requests (POST, PUT, DELETE)

### Implementation Details

**SecurityConfig.java:31-35**
```java
.csrf(csrf -> csrf
    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
    .csrfTokenRequestHandler(requestHandler)
    .ignoringRequestMatchers("/auth/sign-in", "/auth/register")
)
```

- **withHttpOnlyFalse()**: Allows JavaScript to read the CSRF cookie
- **ignoringRequestMatchers**: Login and registration don't require CSRF (they're the first requests)

### Frontend Integration

#### Step 1: Get CSRF Token
After login, call the CSRF endpoint:

```javascript
// After successful login
const response = await fetch('http://localhost:8080/auth/csrf', {
  credentials: 'include'
});
const csrfToken = await response.json();
```

#### Step 2: Include Token in Requests
Add the CSRF token to request headers:

```javascript
fetch('http://localhost:8080/api/users/123', {
  method: 'PUT',
  credentials: 'include',
  headers: {
    'Content-Type': 'application/json',
    'X-XSRF-TOKEN': csrfToken.token  // Include CSRF token
  },
  body: JSON.stringify(userData)
});
```

#### Alternative: Axios Auto-Configuration
```javascript
// Axios automatically reads XSRF-TOKEN cookie and sends it as X-XSRF-TOKEN header
axios.defaults.withCredentials = true;
axios.defaults.xsrfCookieName = 'XSRF-TOKEN';
axios.defaults.xsrfHeaderName = 'X-XSRF-TOKEN';
```

---

## XSS Prevention

### What is XSS?

**Cross-Site Scripting (XSS)** allows attackers to inject malicious scripts into web pages viewed by other users.

### Multi-Layer Defense

#### 1. Security Headers (SecurityConfig.java:42-59)

```java
.headers(headers -> headers
    .xssProtection(xss -> xss.headerValue("1; mode=block"))
    .contentTypeOptions(contentType -> contentType.disable())
    .frameOptions(frame -> frame.deny())
    .contentSecurityPolicy(csp -> csp.policyDirectives(...))
)
```

**Headers Applied:**
- **X-XSS-Protection**: Enables browser XSS filtering
- **X-Content-Type-Options: nosniff**: Prevents MIME-type sniffing
- **X-Frame-Options: DENY**: Prevents clickjacking
- **Content-Security-Policy**: Restricts resource loading sources

#### 2. Input Sanitization (XssFilter.java)

The `XssFilter` wraps all requests and sanitizes parameters by escaping HTML entities:

```java
// Example: "<script>alert('xss')</script>" becomes "&lt;script&gt;alert('xss')&lt;/script&gt;"
```

This prevents malicious HTML/JavaScript from being executed.

#### 3. Output Encoding

When rendering data in views, ensure proper encoding:
- Use Jackson's automatic JSON encoding (already in place)
- For HTML templates (if used), use template engine escaping (Thymeleaf auto-escapes)

#### 4. Validation (Already Implemented)

```java
@NotBlank(message = "Username is required")
@Size(max = 100)
private String username;
```

Jakarta Bean Validation (`@Valid`, `@NotBlank`, `@Size`) provides first-line defense.

---

## Authentication & Authorization

### JWT in HttpOnly Cookies

**AuthController.java:32-37**
```java
Cookie jwtCookie = new Cookie("jwt", token.get());
jwtCookie.setHttpOnly(true);  // JavaScript cannot access (XSS protection)
jwtCookie.setSecure(true);     // HTTPS only (production)
jwtCookie.setPath("/");
jwtCookie.setMaxAge(86400);    // 1 day
```

### Role-Based Access Control

**SecurityConfig.java:36-40**
```java
.authorizeHttpRequests((authz) -> authz
    .requestMatchers("/auth/**").permitAll()
    .requestMatchers("/api/admin/**").hasRole("ADMIN")
    .requestMatchers("/api/user/**").hasRole("USER")
    .anyRequest().authenticated()
)
```

### Public Endpoints

- `POST /auth/register` - User self-registration (USER role)
- `POST /auth/sign-in` - Login
- `GET /auth/csrf` - Get CSRF token

---

## Security Headers

### Applied Headers

| Header | Value | Purpose |
|--------|-------|---------|
| X-XSS-Protection | 1; mode=block | Browser XSS filter |
| X-Content-Type-Options | nosniff | Prevent MIME sniffing |
| X-Frame-Options | DENY | Prevent clickjacking |
| Content-Security-Policy | See below | Restrict resource sources |

### Content Security Policy (CSP)

```
default-src 'self';
script-src 'self';
style-src 'self' 'unsafe-inline';
img-src 'self' data:;
font-src 'self';
connect-src 'self';
frame-ancestors 'none';
```

**Customization**: Update `SecurityConfig.java:50-57` if you need to allow external resources (CDNs, analytics, etc.)

---

## Frontend Integration

### Complete Example (React)

```javascript
// 1. Login
async function login(username, password) {
  const response = await fetch('http://localhost:8080/auth/sign-in', {
    method: 'POST',
    credentials: 'include',  // Important: Include cookies
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  });

  if (response.ok) {
    // 2. Get CSRF token
    const csrfResponse = await fetch('http://localhost:8080/auth/csrf', {
      credentials: 'include'
    });
    const csrfToken = await csrfResponse.json();

    // 3. Store CSRF token (in memory, not localStorage for security)
    sessionStorage.setItem('csrf-token', csrfToken.token);
  }
}

// 4. Make authenticated requests
async function updateUser(userId, userData) {
  const csrfToken = sessionStorage.getItem('csrf-token');

  const response = await fetch(`http://localhost:8080/api/users/${userId}`, {
    method: 'PUT',
    credentials: 'include',  // Include JWT cookie
    headers: {
      'Content-Type': 'application/json',
      'X-XSRF-TOKEN': csrfToken  // Include CSRF token
    },
    body: JSON.stringify(userData)
  });

  return response.json();
}
```

### CORS Configuration

Update `WebSecurityConfig.java:14` with your frontend URLs:

```java
.allowedOrigins(
  "http://localhost:3000",     // Development
  "https://yourdomain.com"      // Production
)
```

---

## Best Practices

### Do's ✓

1. **Always use HTTPS in production** (JWT cookies have `Secure` flag)
2. **Store CSRF token in sessionStorage**, not localStorage
3. **Include credentials in all requests** (`credentials: 'include'`)
4. **Validate user input** on both frontend and backend
5. **Use parameterized queries** (JPA handles this automatically)
6. **Keep dependencies updated** (`mvn versions:display-dependency-updates`)
7. **Change default admin password** (`admin/admin123`) immediately
8. **Use environment variables** for secrets (database passwords, JWT secret)

### Don'ts ✗

1. **Don't disable CSRF protection** for cookie-based auth
2. **Don't store JWT in localStorage** (XSS vulnerability)
3. **Don't trust user input** - always validate and sanitize
4. **Don't expose stack traces** in production errors
5. **Don't commit secrets** to version control (use .env files)
6. **Don't use weak passwords** - enforce password policies
7. **Don't skip HTTPS** in production

---

## Security Checklist

### Before Deployment

- [ ] Change default admin credentials
- [ ] Configure production CORS origins
- [ ] Enable HTTPS (TLS/SSL)
- [ ] Set strong JWT secret (use environment variable)
- [ ] Review and update CSP policies
- [ ] Enable database encryption at rest
- [ ] Set up rate limiting (DDoS protection)
- [ ] Configure logging and monitoring
- [ ] Perform security audit/penetration testing
- [ ] Review dependency vulnerabilities (`mvn dependency-check:check`)

### Regular Maintenance

- [ ] Update dependencies monthly
- [ ] Review access logs for suspicious activity
- [ ] Rotate JWT secrets periodically
- [ ] Audit user permissions quarterly
- [ ] Test backup and recovery procedures

---

## Attack Scenarios & Mitigations

### Scenario 1: XSS Attack

**Attack**: User enters `<script>alert('xss')</script>` in username field

**Mitigation**:
1. `XssFilter` sanitizes to `&lt;script&gt;alert('xss')&lt;/script&gt;`
2. `@Size` validation limits input length
3. CSP blocks inline script execution
4. HttpOnly cookies prevent JWT theft

### Scenario 2: CSRF Attack

**Attack**: Malicious site tries to make authenticated request

**Mitigation**:
1. CSRF token required for state-changing operations
2. Token stored in cookie (same-site)
3. Backend validates token on every request
4. Attacker cannot read the token (cross-origin restriction)

### Scenario 3: SQL Injection

**Attack**: User enters `admin' OR '1'='1` in username

**Mitigation**:
1. JPA uses parameterized queries automatically
2. Input validation rejects invalid characters
3. `@NotBlank` prevents empty/malicious input

### Scenario 4: Brute Force Attack

**Current Gap**: Not yet implemented

**Recommendation**: Add rate limiting (Spring Security + Bucket4j)

---

## Testing Security

### Test CSRF Protection

```bash
# This should FAIL (403 Forbidden)
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -b "jwt=<your-jwt-token>" \
  -d '{"username":"test"}'

# This should SUCCEED (with CSRF token)
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -H "X-XSRF-TOKEN: <csrf-token>" \
  -b "jwt=<your-jwt-token>; XSRF-TOKEN=<csrf-token>" \
  -d '{"username":"test"}'
```

### Test XSS Protection

```bash
# Submit XSS payload - should be escaped
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "<script>alert(\"xss\")</script>",
    "password": "test123",
    "email": "test@test.com",
    "phoneNumber": "1234567890"
  }'

# Check database - value should be escaped
```

---

## Additional Resources

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [CSRF Prevention Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html)
- [XSS Prevention Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Cross_Site_Scripting_Prevention_Cheat_Sheet.html)

---

## Contact & Reporting

For security vulnerabilities, please report privately to the development team rather than creating public issues.
