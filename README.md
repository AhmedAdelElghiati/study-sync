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
├── config/          # Security config and JWT filter
├── controller/      # REST endpoints (auth, students, instructors, courses, enrollments, materials)
├── dto/             # Request/response records with Jakarta Validation
├── entity/          # JPA entities and inheritance model
├── enums/           # String-backed enums used by entities and DTOs
├── exception/       # Custom exceptions and global handlers
├── repository/      # Spring Data JPA repositories
├── service/         # Business logic and JWT support
├── shared/          # Shared API response helpers
└── util/            # Utility helpers
```

## Domain highlights

- `User` uses a UUID primary key and `JOINED` inheritance strategy with subtypes: `Student`, `Instructor`, `Professor`, and `BatchRepresentive`.
- Enum fields are persisted as strings (`@Enumerated(EnumType.STRING)`): `Department`, `Role`, `Level`, `ApprovalStatus`, `Semester`, `EnrollmentStatus`, `MaterialType`, and `InstructorType`.
- DTOs are record-based and validation-heavy.
- `StudentResponse` and `InstructorResponse` include `approvalStatus` and `createdAt`.
- `StudentCreateRequest` and `InstructorCreateRequest` enforce university email (`@ics.tanta.edu.eg`) and password complexity rules.
- `InstructorType` uses mixed-case enum constants: `Professor`, `TeachingAssistant`.
- `Course` entities link to an instructor and department, with semester and level tracking.
- `Enrollment` tracks student participation in courses with status transitions.
- `CourseMaterial` classifies learning resources by type and associates with courses.

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

### Courses (`/api/courses`)

- `POST /api/courses`
- `GET /api/courses`
- `GET /api/courses/{id}`
- `PUT /api/courses/{id}`
- `DELETE /api/courses/{id}`

Course entities support department, semester, and instructor assignment.

### Enrollments (`/api/enrollments`)

- `POST /api/enrollments`
- `GET /api/enrollments`
- `GET /api/enrollments/{id}`
- `PUT /api/enrollments/{id}`
- `DELETE /api/enrollments/{id}`

Tracks student enrollment in courses with status (ENROLLED, COMPLETED, DROPPED).

### Course Materials (`/api/course-materials`)

- `POST /api/course-materials`
- `GET /api/course-materials`
- `GET /api/course-materials/{id}`
- `PUT /api/course-materials/{id}`
- `DELETE /api/course-materials/{id}`

Manages course materials (lectures, assignments, etc.) with type classification.

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

## Docker deployment

A `Dockerfile` is provided for containerization. Build and run the application in Docker:

```bash
docker build -t studysync:latest .
docker run -p 8080:8080 \
  -e DB_USERNAME=your-username \
  -e DB_PASSWORD=your-password \
  -e SECRET_KEY=your-very-long-random-secret \
  studysync:latest
```

## Current test coverage

The test suite currently contains one Spring context-load test:

- `src/test/java/elghiati/studysync/StudysyncApplicationTests.java`

## Development guidelines

When adding new features, follow these conventions:

1. **Primary Keys**: Use UUID for all persisted entities (extends from `User`).
2. **Enums**: Always use `@Enumerated(EnumType.STRING)` to persist enums as strings, not ordinals.
3. **DTOs**: Create separate request/response record classes with Jakarta Validation annotations. Keep them independent from entity classes.
4. **Inheritance**: The user model uses JPA `JOINED` strategy; maintain this pattern for polymorphic queries.
5. **Email validation**: University-affiliated users (students, instructors) must use `@ics.tanta.edu.eg` domain.
6. **Build tool**: Always use the Maven Wrapper (`./mvnw` / `mvnw.cmd`) instead of a system Maven installation.
7. **Java version**: Target Java 25; ensure your JDK toolchain matches.
