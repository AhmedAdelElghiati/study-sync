# Studysync

Studysync is a Spring Boot 4.0.5 backend for managing students and instructors. The application is organized around a JPA inheritance model where `User` is the base entity and `Student` / `Instructor` extend it.

## Tech stack

- Java 25
- Spring Boot 4.0.5
- Spring Web MVC
- Spring Data JPA
- Spring Security
- Spring Validation
- PostgreSQL
- Lombok
- Maven Wrapper (`mvnw` / `mvnw.cmd`)

## Project structure

```text
src/main/java/elghiati/studysync/
├── config/        # Spring configuration, including security
├── controller/    # REST controllers
├── dto/           # Request / response records with validation
├── entity/        # JPA entities and inheritance model
├── enums/         # String-backed enums used by entities and DTOs
├── exception/     # Global exception handling and custom exceptions
├── repository/    # Spring Data JPA repositories
├── service/       # Business logic
├── shared/        # Shared API response helpers
└── util/          # Utility helpers
```

## Domain highlights

- `User` uses a UUID primary key and the `JOINED` inheritance strategy.
- Enum fields are stored as strings in the database.
- `StudentResponse` and `InstructorResponse` include `approvalStatus` and `createdAt`.
- `StudentCreateRequest` and `InstructorCreateRequest` enforce validation rules, including the `@ics.tanta.edu.eg` university email format and password complexity.
- `InstructorType` currently uses mixed-case enum constants (`Professor`, `TeachingAssistant`).

## API summary

### Students

- `POST /api/students/register`
- `GET /api/students/{id}`
- `PUT /api/students/{id}`
- `DELETE /api/students/{id}`

### Instructors

- `POST /api/instructors/register`
- `GET /api/instructors/{id}`
- `PUT /api/instructors/{id}`
- `DELETE /api/instructors/{id}`

Responses are wrapped in the shared `APIResponse` type.

## Configuration

The application reads database settings from `src/main/resources/application.properties`.

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/study_sync
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

Set the following environment variables before starting the app:

- `DB_USERNAME`
- `DB_PASSWORD`

Additional defaults:

- `spring.jpa.hibernate.ddl-auto=none`
- `spring.jpa.show-sql=true`
- `spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect`

## Prerequisites

- JDK 25
- PostgreSQL running locally with a `study_sync` database
- Environment variables for the database credentials

> Use the Maven Wrapper instead of a system Maven installation.

## Run the application

### Windows PowerShell

```powershell
$env:DB_USERNAME="your-username"
$env:DB_PASSWORD="your-password"
.\mvnw.cmd spring-boot:run
```

### macOS / Linux / Git Bash

```bash
export DB_USERNAME="your-username"
export DB_PASSWORD="your-password"
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

> If compilation fails with a Java release/version mismatch, make sure the build is using JDK 25.

## Security status

`SecurityConfig` currently disables CSRF and permits all requests. Authentication and authorization are not enforced yet.

## Tests

The current test suite contains a single Spring context load test:

- `src/test/java/elghiati/studysync/StudysyncApplicationTests.java`
