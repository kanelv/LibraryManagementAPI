# Library Management System

A production-grade library management system built with **Domain-Driven Design (DDD)**, **Clean Architecture**, and **Hexagonal Architecture** principles. This project demonstrates enterprise-level Java development practices with a focus on maintainability, testability, and scalability.

## Table of Contents
- [Architecture Overview](#architecture-overview)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Design Patterns & Principles](#design-patterns--principles)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Database Management](#database-management)
- [Security](#security)
- [Development Guidelines](#development-guidelines)

---

## Architecture Overview

This project follows **Clean Architecture** with **DDD** tactical patterns, organized into distinct layers:

```
┌─────────────────────────────────────────────────────────┐
│                   Interfaces Layer                      │
│              (REST Controllers, DTOs)                   │
└──────────────────┬──────────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────────┐
│               Application Layer                         │
│        (Use Cases, Application Services)                │
└──────────────────┬──────────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────────┐
│                 Domain Layer                            │
│   (Entities, Value Objects, Business Logic)             │
│         ◄── No dependencies on outer layers             │
└──────────────────▲──────────────────────────────────────┘
                   │
┌──────────────────┴──────────────────────────────────────┐
│            Infrastructure Layer                          │
│  (JPA Entities, Repositories, Mappers, Security)         │
└─────────────────────────────────────────────────────────┘
```

### Key Architectural Decisions

1. **Domain Layer Independence**: Domain entities contain business logic and have zero dependencies on frameworks
2. **Repository Pattern**: Domain repositories (interfaces) are implemented by infrastructure adapters
3. **Mapper Pattern**: Clean separation between domain entities and persistence entities (JPA)
4. **Use Case Pattern**: Each business operation is a single-purpose use case class
5. **Value Objects**: Immutable objects for domain concepts (ISBN, Email, Role, etc.)

---

## Technology Stack

### Core Framework
- **Spring Boot** 3.3.3
- **Java** 17
- **Maven** 3

### Persistence
- **Spring Data JPA** (Hibernate 6)
- **PostgreSQL** 15
- **Flyway** (Database migrations)

### Security
- **Spring Security** 6
- **JWT** (JWT token-based authentication)
- **BCrypt** (Password encryption)

### Development Tools
- **Lombok** (Boilerplate reduction)
- **Jakarta Validation** (Input validation)

---

## Project Structure

```
src/main/java/com/kane/librarymanagement/
│
├── domain/                          # Domain Layer (Business Logic)
│   ├── book/
│   │   ├── Book.java               # Domain Entity (rich model)
│   │   ├── ISBN.java               # Value Object
│   │   ├── BookId.java             # Value Object
│   │   └── BookRepository.java     # Domain Interface
│   ├── user/
│   │   ├── User.java               # Domain Entity
│   │   ├── Role.java               # Value Object (immutable)
│   │   ├── Email.java              # Value Object
│   │   ├── UserId.java             # Value Object
│   │   └── UserRepository.java     # Domain Interface
│   ├── enums/
│   │   ├── RoleType.java
│   │   └── BorrowStatus.java
│   └── shared/
│       └── exceptions/
│           └── BusinessException.java
│
├── application/                     # Application Layer (Use Cases)
│   ├── auth/
│   │   └── usecases/
│   │       └── SignInUseCase.java
│   ├── book/
│   │   ├── dto/                    # Request/Response DTOs
│   │   │   ├── CreateBookRequest.java
│   │   │   ├── UpdateBookRequest.java
│   │   │   └── BookResponse.java
│   │   └── usecases/
│   │       ├── CreateBookUseCase.java
│   │       ├── UpdateBookUseCase.java
│   │       ├── DeleteBookUseCase.java
│   │       ├── GetOneBookUseCase.java
│   │       └── ListBookUseCase.java
│   ├── user/
│   │   ├── dto/
│   │   └── usecases/
│   └── common/
│       └── services/
│           └── JwtService.java
│
├── infrastructure/                  # Infrastructure Layer (Adapters)
│   ├── persistence/
│   │   ├── repositories/           # Repository Implementations (Adapters)
│   │   │   ├── BookRepositoryImpl.java
│   │   │   └── UserRepositoryImpl.java
│   │   └── jpa/
│   │       ├── entities/           # JPA Entities (Persistence Models)
│   │       │   ├── Book.java
│   │       │   ├── User.java
│   │       │   └── Borrowing.java
│   │       ├── repositories/       # Spring Data JPA Interfaces
│   │       │   ├── BookJpaRepository.java
│   │       │   └── UserJpaRepository.java
│   │       └── mappers/            # Domain ↔ JPA Mappers
│   │           ├── BookMapper.java
│   │           └── UserMapper.java
│   ├── security/
│   │   └── SecurityConfig.java
│   └── intercepts/
│       └── JwtRequestFilter.java
│
├── interfaces/                      # Interface Layer (API)
│   └── rest/
│       └── controllers/
│           ├── AuthController.java
│           ├── BookController.java
│           └── UserController.java
│
└── config/                          # Configuration
    └── ApplicationConfig.java

src/main/resources/
├── application.properties
└── db/migration/                    # Flyway migrations
    ├── V2__create_member_table.sql
    ├── V3__create_book_table.sql
    └── V4__create_book_history_table.sql
```

---

## Design Patterns & Principles

### Implemented Patterns

1. **Repository Pattern**
   - Domain repositories define contracts
   - Infrastructure provides JPA implementations
   - Example: `BookRepository` (domain) ← `BookRepositoryImpl` (infrastructure)

2. **Mapper Pattern**
   - Separates domain models from persistence models
   - Prevents JPA annotations polluting domain layer
   - Example: `BookMapper.toDomain()`, `BookMapper.toJpa()`

3. **Value Object Pattern**
   - Immutable objects representing domain concepts
   - Built-in validation
   - Examples: `ISBN`, `Email`, `Role`, `BookId`

4. **Use Case Pattern**
   - Single Responsibility - one use case per operation
   - Example: `CreateBookUseCase`, `UpdateBookUseCase`

5. **Builder Pattern**
   - Fluent API for constructing complex domain entities
   - Validates invariants at build time
   - Example: `User.builder().username("john").email(Email.of("john@example.com")).build()`

### SOLID Principles

- **Single Responsibility**: Each use case handles one business operation
- **Open/Closed**: Repository implementations can be swapped without changing domain
- **Liskov Substitution**: Domain interfaces can be implemented by any adapter
- **Interface Segregation**: Repositories expose only needed methods
- **Dependency Inversion**: Domain depends on abstractions, not implementations

### DDD Tactical Patterns

- **Entities**: `Book`, `User` (with identity and lifecycle)
- **Value Objects**: `ISBN`, `Email`, `Role` (immutable, compared by value)
- **Aggregates**: `User` is the aggregate root for borrowed books
- **Repositories**: Abstract data access for aggregates
- **Domain Services**: Business logic that doesn't belong to entities

---

## Getting Started

### Prerequisites

- **Java 17** or higher
- **PostgreSQL 15** or higher
- **Maven 3.8+**

### Environment Setup

1. **Create PostgreSQL database:**
```sql
CREATE DATABASE library_management_db;
```

2. **Configure database connection:**

Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/library_management_db
spring.datasource.username=postgres
spring.datasource.password=your_password
```

3. **Install dependencies:**
```bash
mvn clean install
```

4. **Run Flyway migrations:**
```bash
# Initialize Flyway (first time only)
mvn flyway:baseline

# Apply migrations
mvn flyway:migrate
```

5. **Start the application:**
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

---

## API Documentation

### Authentication

#### Sign In
```http
POST /api/auth/signin
Content-Type: application/json

{
  "username": "admin",
  "password": "password123"
}

Response: 200 OK
Set-Cookie: accessToken=<jwt_token>; HttpOnly
```

### Books API

#### Create Book (Admin only)
```http
POST /api/books
Authorization: Bearer <token>
Content-Type: application/json

{
  "isbn": "978-0-596-52068-7",
  "title": "Head First Design Patterns",
  "author": "Eric Freeman",
  "publisher": "O'Reilly Media",
  "publishedYear": 2004,
  "genre": "Computer Science",
  "totalCopies": 10
}

Response: 201 Created
```

#### List Books
```http
GET /api/books
Authorization: Bearer <token>

Response: 200 OK
[
  {
    "id": 1,
    "isbn": "978-0-596-52068-7",
    "title": "Head First Design Patterns",
    "author": "Eric Freeman",
    "totalCopies": 10,
    "availableCopies": 8,
    "createdAt": "2024-03-13T10:30:00Z"
  }
]
```

#### Get Book by ISBN
```http
GET /api/books/{isbn}
Authorization: Bearer <token>

Response: 200 OK
```

#### Update Book (Admin only)
```http
PUT /api/books/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Updated Title",
  "author": "Updated Author",
  "totalCopies": 15
}

Response: 200 OK
```

#### Delete Book (Admin only)
```http
DELETE /api/books/{id}
Authorization: Bearer <token>

Response: 204 No Content
```

### Users API

Similar RESTful endpoints for user management at `/api/users`

---

## Database Management

### Flyway Migration Guidelines

**Important Rules:**

1. **Never modify existing migration files** in `src/main/resources/db/migration/`
2. **Always create new migration files** for schema changes
3. **Naming Convention:**
   ```
   V{version}__{description}.sql

   Examples:
   V2__create_member_table.sql
   V3__create_book_table.sql
   V7__add_role_fields_to_user_table.sql
   ```

### Migration Commands

```bash
# Initialize Flyway (first time setup)
mvn flyway:baseline

# Apply pending migrations
mvn flyway:migrate

# Validate applied migrations
mvn flyway:validate

# View migration info
mvn flyway:info
```

### Verify Migrations

Check applied migrations in PostgreSQL:
```sql
SELECT * FROM flyway_schema_history ORDER BY installed_rank;
```

---

## Security

### Authentication & Authorization

- **JWT-based authentication** with HTTP-only cookies
- **BCrypt password hashing** (cost factor: 10)
- **Role-based access control** (RBAC):
  - `ADMIN`: Full access (CRUD on all resources)
  - `USER`: Read access + borrow/return books
  - `GUEST`: Limited read access

### Role Configuration

Roles are implemented as **Value Objects** with denormalized permissions:

```java
Role.admin()  → maxBookNumber: 20, maxBorrowDuration: 60 days
Role.user()   → maxBookNumber: 5,  maxBorrowDuration: 14 days
Role.guest()  → maxBookNumber: 2,  maxBorrowDuration: 7 days
```

### Protected Endpoints

Controllers use `@PreAuthorize` for method-level security:
```java
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<BookResponse> createBook(...)

@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public ResponseEntity<List<BookResponse>> listBooks(...)
```

---

## Development Guidelines

### Adding a New Feature

1. **Domain First**: Create domain entities, value objects, and repository interfaces
2. **Use Cases**: Implement application use cases with business logic
3. **Infrastructure**: Add JPA entities, mappers, and repository implementations
4. **Interface**: Create REST controllers and DTOs
5. **Migration**: Add Flyway migration if database changes are needed

### Code Quality Standards

- **Business logic** belongs in domain entities/value objects
- **Use case classes** should be thin orchestrators
- **Validation** happens at multiple layers:
  - Domain: Business invariants (e.g., `ISBN` format)
  - Application: Use case preconditions
  - Interface: Input validation with Jakarta Validation
- **Mappers** must be used to convert between layers (no JPA entities in domain)

### Testing Strategy

- **Domain Layer**: Unit tests for business logic
- **Use Cases**: Integration tests with mocked repositories
- **Controllers**: Spring MockMvc tests
- **Repositories**: Spring Data JPA tests with test database

---

## Architecture Benefits

### Why This Architecture?

1. **Testability**: Domain logic testable without framework dependencies
2. **Maintainability**: Clear separation of concerns across layers
3. **Flexibility**: Easy to swap infrastructure (e.g., PostgreSQL → MongoDB)
4. **Scalability**: Use cases can be independently optimized
5. **Team Collaboration**: Different teams can work on different layers
6. **Business Focus**: Domain layer reflects business requirements clearly

### Trade-offs

- **Complexity**: More layers = more code (justified for long-term projects)
- **Learning Curve**: Team needs DDD knowledge
- **Performance**: Mapping overhead (negligible in most cases)

---

## Contributing

When contributing, ensure:
1. Domain logic stays in the domain layer
2. All new features include tests
3. Database changes include Flyway migrations
4. API changes are documented
5. Follow existing naming conventions

---

## References

- [Domain-Driven Design by Eric Evans](https://www.domainlanguage.com/ddd/)
- [Clean Architecture by Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Spring Data JPA Documentation](https://spring.io/projects/spring-data-jpa)
- [Flyway Documentation](https://flywaydb.org/documentation/)

---

## License

MIT License
