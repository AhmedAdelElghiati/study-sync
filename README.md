# Studysync

### Brief

Studysync is a university learning-management backend API built with Spring Boot. It currently provides JWT-secured flows for authentication, user management, courses, enrollments, tasks, and course materials (including file uploads).

## Overview

Studysync follows a layered architecture under `elghiati.studysync` (`controller`, `service`, `repository`, `entity`, and `dto`).

- Main class: `StudysyncApplication`
- API style: REST + JSON, wrapped in `APIResponse<T>`
- Auth model: JWT bearer tokens
- Persistence: Spring Data JPA + PostgreSQL
- Domain model: `User` inheritance with role-based behavior

## Tech stack

- Java 25
- Spring Boot 4.0.5
- Spring Web MVC
- Spring Data JPA
- Spring Security
- Jakarta Validation
- PostgreSQL
- Springdoc OpenAPI (Swagger UI)
- Cloudinary (media storage integration)
- Lombok
- Maven Wrapper (`mvnw` / `mvnw.cmd`)

## Project structure

```text
src/main/java/elghiati/studysync/
├── config/       # Security and app-level configuration
├── controller/   # REST endpoints
├── dto/          # Request/response records with validation
├── entity/       # JPA entities
├── enums/        # Enum types persisted as strings
├── exception/    # Custom exceptions + global handler
├── repository/   # Spring Data JPA repositories
├── service/      # Business logic
├── shared/       # Shared API response wrapper(s)
└── util/         # Utility helpers (e.g., access validators)
```

## Domain highlights

- `User` uses UUID primary key and JPA `JOINED` inheritance.
- Current concrete user entities include `Student` and `Instructor`.
- The project also contains role/type enums and follows string-backed enum persistence via `@Enumerated(EnumType.STRING)`.
- `InstructorType` intentionally uses mixed-case constants: `Professor`, `TeachingAssistant`.
- DTOs are record-based and validation-focused.
- Registration DTOs enforce university email rules (`@ics.tanta.edu.eg`) and password complexity.
- `Course` tracks department, semester, level, and instructor ownership.
- `Enrollment` tracks student-course membership and enrollment status.
- `Task` and `CourseMaterial` are course-scoped resources with access checks.

## API summary

All successful responses are wrapped in `APIResponse<T>`.

### Auth (`/api/auth`)

- `POST /api/auth/register/student`
- `POST /api/auth/register/instructor`
- `POST /api/auth/login`

### Students (`/api/students`)

- `POST /api/students/register`
- `GET /api/students/{id}`
- `PUT /api/students/{id}`
- `DELETE /api/students/{id}`

### Instructors (`/api/instructors`)

- `POST /api/instructors/register`
- `GET /api/instructors`
- `GET /api/instructors/{id}`
- `PUT /api/instructors/{id}`
- `DELETE /api/instructors/{id}`

### Courses (`/api/courses`)

- `POST /api/courses`
- `PUT /api/courses/{courseId}`
- `DELETE /api/courses/{courseId}`
- `GET /api/courses` (student view)
- `GET /api/courses/my-courses` (instructor view)
- `GET /api/courses/{courseId}`

### Enrollments (`/api/enrollments`)

- `POST /api/enrollments/{courseId}`
- `DELETE /api/enrollments/{courseId}`
- `GET /api/enrollments`

### Course Materials (`/api/courses/{courseId}/materials`)

- `POST /api/courses/{courseId}/materials` (multipart upload)
- `PUT /api/courses/{courseId}/materials/{materialId}`
- `DELETE /api/courses/{courseId}/materials/{materialId}`
- `GET /api/courses/{courseId}/materials`

### Tasks (`/api/courses/{courseId}/tasks`)

- `POST /api/courses/{courseId}/tasks`
- `PUT /api/courses/{courseId}/tasks/{taskId}`
- `DELETE /api/courses/{courseId}/tasks/{taskId}`
- `GET /api/courses/{courseId}/tasks`

## Security behavior

- Stateless API (`SessionCreationPolicy.STATELESS`) with JWT authentication filter.
- Public endpoints: `/api/auth/**`.
- URL-level restrictions:
  - `/api/students/**` requires role `STUDENT`.
  - `/api/instructors/**` requires role `INSTRUCTOR`.
- Additional restrictions are enforced via method-level `@PreAuthorize` (for example, professor-only course creation).
- Swagger paths are excluded from filtering:
  - `/v3/api-docs`
  - `/v3/api-docs/**`
  - `/swagger-ui/**`
  - `/swagger-ui.html`
- Configured CORS origins:
  - `http://localhost:4200`
  - `https://study-sync-two-psi.vercel.app`

## Configuration

`src/main/resources/application.properties` uses environment variables for secrets and external services:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

app.jwt.secret=${SECRET_KEY}
app.jwt.expiration=86400000

cloudinary.cloud-name=${CLOUDINARY_CLOUD_NAME}
cloudinary.api-key=${CLOUDINARY_API_KEY}
cloudinary.api-secret=${CLOUDINARY_API_SECRET}
```

Also configured:

- `spring.jpa.hibernate.ddl-auto=update`
- `spring.jpa.show-sql=true`
- `spring.jpa.hibernate.format_sql=true`
- `spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect`
- `spring.servlet.multipart.max-file-size=10MB`
- `spring.servlet.multipart.max-request-size=10MB`

Required environment variables:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `SECRET_KEY`
- `CLOUDINARY_CLOUD_NAME`
- `CLOUDINARY_API_KEY`
- `CLOUDINARY_API_SECRET`

## Prerequisites

- JDK 25 toolchain
- PostgreSQL database
- Cloudinary account (for file upload endpoints)
- Maven Wrapper usage (`mvnw` / `mvnw.cmd`)

> If you get `release version 25 not supported`, your current JDK is not Java 25.

## Run the application

### Windows (PowerShell)

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/study_sync"
$env:DB_USERNAME="your-username"
$env:DB_PASSWORD="your-password"
$env:SECRET_KEY="your-very-long-random-secret"
$env:CLOUDINARY_CLOUD_NAME="your-cloudinary-cloud-name"
$env:CLOUDINARY_API_KEY="your-cloudinary-api-key"
$env:CLOUDINARY_API_SECRET="your-cloudinary-api-secret"
.\mvnw.cmd spring-boot:run
```

### macOS / Linux / Git Bash

```bash
export DB_URL="jdbc:postgresql://localhost:5432/study_sync"
export DB_USERNAME="your-username"
export DB_PASSWORD="your-password"
export SECRET_KEY="your-very-long-random-secret"
export CLOUDINARY_CLOUD_NAME="your-cloudinary-cloud-name"
export CLOUDINARY_API_KEY="your-cloudinary-api-key"
export CLOUDINARY_API_SECRET="your-cloudinary-api-secret"
./mvnw spring-boot:run
```

## API docs (Swagger)

When the app is running:

- UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Run tests

### Windows

```powershell
.\mvnw.cmd test
```

### macOS / Linux / Git Bash

```bash
./mvnw test
```

Current automated tests include Spring context startup test:

- `src/test/java/elghiati/studysync/StudysyncApplicationTests.java`

## Docker

Use the included `Dockerfile`:

```bash
docker build -t studysync:latest .
docker run -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/study_sync \
  -e DB_USERNAME=your-username \
  -e DB_PASSWORD=your-password \
  -e SECRET_KEY=your-very-long-random-secret \
  -e CLOUDINARY_CLOUD_NAME=your-cloudinary-cloud-name \
  -e CLOUDINARY_API_KEY=your-cloudinary-api-key \
  -e CLOUDINARY_API_SECRET=your-cloudinary-api-secret \
  studysync:latest
```

## Development conventions

1. Keep UUID keys for persisted user hierarchy entities.
2. Persist enums as strings (`@Enumerated(EnumType.STRING)`).
3. Keep request/response DTOs separate from entities.
4. Preserve validation-heavy DTO style.
5. Keep `InstructorType` values exactly as implemented (`Professor`, `TeachingAssistant`).
6. Always use Maven Wrapper for build/test commands.
