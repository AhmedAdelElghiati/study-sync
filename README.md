# Studysync

Studysync is a Spring Boot 4.0.5 backend for managing students and instructors. The domain model is centered on JPA inheritance where `User` is the base entity and specialized user types (such as `Student` and `Instructor`) extend it.

## Tech stack

- Java 25
- Spring Boot 4.0.5
- Spring Web MVC
- Spring Data JPA
- Spring Security (JWT-based authentication)
- Spring Validation
- PostgreSQL
- Springdoc OpenAPI (Swagger UI)
- Lombok
- Maven Wrapper (`mvnw` / `mvnw.cmd`)

## Project structure

```text
src/main/java/elghiati/studysync/
├── config/        # Security config and JWT filter
├── controller/    # REST endpoints (auth, students, instructors)
├── dto/           # Request/response records with Jakarta Validation
├── entity/        # JPA entities and inheritance model
├── enums/         # String-backed enums used by entities and DTOs
├── exception/     # Custom exceptions and global handlers
├── repository/    # Spring Data JPA repositories
├── service/       # Business logic and JWT support
├── shared/        # Shared API response helpers
└── util/          # Utility helpers
```

## Domain highlights

- `User` uses a UUID primary key and `JOINED` inheritance strategy.
- Enum fields are persisted as strings (`@Enumerated(EnumType.STRING)`).
- DTOs are record-based and validation-heavy.
- `StudentResponse` and `InstructorResponse` include `approvalStatus` and `createdAt`.
- `StudentCreateRequest` and `InstructorCreateRequest` enforce university email (`@ics.tanta.edu.eg`) and password complexity rules.
- `InstructorType` uses mixed-case enum constants: `Professor`, `TeachingAssistant`.

## API summary

Responses are wrapped in `APIResponse<T>`.

### Auth (`/api/auth`)

- `POST /api/auth/register/student`
- `POST /api/auth/register/instructor`
- `POST /api/auth/login`

Returns `AuthResponse` with `token`, `role`, and `userId`.

### Students (`/api/students`)

- `POST /api/students/register`
- `GET /api/students/{id}`
- `PUT /api/students/{id}`
- `DELETE /api/students/{id}`

### Instructors (`/api/instructors`)

- `POST /api/instructors/register`
- `GET /api/instructors/{id}`
- `PUT /api/instructors/{id}`
- `DELETE /api/instructors/{id}`

## Security behavior

- CSRF is disabled and the app is stateless (`SessionCreationPolicy.STATELESS`).
- `/api/auth/**` is publicly accessible.
- `/api/students/**` requires role `STUDENT`.
- `/api/instructors/**` requires role `INSTRUCTOR`.
- JWT bearer tokens are processed by `JwtAuthenticationFilter` from the `Authorization: Bearer <token>` header.
- Swagger endpoints are excluded from security filtering:
  - `/v3/api-docs`
  - `/v3/api-docs/**`
  - `/swagger-ui/**`
  - `/swagger-ui.html`

## Configuration

Database and JWT settings are defined in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/study_sync
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
app.jwt.secret=${SECRET_KEY}
app.jwt.expiration=86400000
```

Set these environment variables before running the app:

- `DB_USERNAME`
- `DB_PASSWORD`
- `SECRET_KEY`

Additional defaults:

- `spring.jpa.hibernate.ddl-auto=none`
- `spring.jpa.show-sql=true`
- `spring.jpa.hibernate.format_sql=true`
- `spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect`

## Prerequisites

- JDK 25
- PostgreSQL running locally with a `study_sync` database
- Environment variables for DB credentials and JWT secret

Use the Maven Wrapper instead of a system Maven installation.

## Run the application

### Windows PowerShell

```powershell
$env:DB_USERNAME="your-username"
$env:DB_PASSWORD="your-password"
$env:SECRET_KEY="your-very-long-random-secret"
.\mvnw.cmd spring-boot:run
```

### macOS / Linux / Git Bash

```bash
export DB_USERNAME="your-username"
export DB_PASSWORD="your-password"
export SECRET_KEY="your-very-long-random-secret"
./mvnw spring-boot:run
```

## Run tests

### Windows PowerShell

```powershell
.\mvnw.cmd test
```

### macOS / Linux / Git Bash

```bash
./mvnw test
```

If compilation fails with a Java release/version mismatch, make sure your build is using JDK 25.

## Current test coverage

The test suite currently contains one Spring context-load test:

- `src/test/java/elghiati/studysync/StudysyncApplicationTests.java`
