# Studysync agent guide
## Project shape
- Spring Boot 4.0.5 backend in `elghiati.studysync`; the app starts from `StudysyncApplication`.
- Layers are already laid out by package: `entity/`, `dto/`, `repository/`, `service/`, with `controller/`, `exception/`, and `util/` currently empty.
- The domain model is centered on JPA inheritance: `User` is the base entity (`users` table, `JOINED` strategy), with `Student`, `Instructor`, `Professor`, and `BatchRepresentitve` as subclasses.
## Domain conventions to preserve
- Use UUID primary keys for persisted users (`UserRepository extends JpaRepository<User, UUID>`).
- Keep enum fields stored as strings (`@Enumerated(EnumType.STRING)` on `Department`, `Role`, `Level`, `ApprovalStatus`, `InstructorType`).
- DTOs are records with Jakarta Validation annotations; `StudentCreateRequest` shows the current style, including the university-email rule for `@ics.tanta.edu.eg` and password complexity checks.
- `StudentResponse` mirrors persisted data plus `approvalStatus` and `createdAt`; keep request/response DTOs separate from entity classes.
- `InstructorType` currently uses mixed-case enum constants (`Professor`, `TeachingAssistant`), unlike the other uppercase enums—match the existing names exactly.
- Task max grade is available in `Task` entity.
## Build, test, and debug workflow
- Use the Maven wrapper, not a system Maven install: Windows `mvnw.cmd test`, other shells `./mvnw test`.
- This project targets Java 25 in `pom.xml`; the current environment failed with `release version 25 not supported`, so switch to a JDK 25 toolchain before compiling.
- PostgreSQL is configured in `src/main/resources/application.properties` for local database `study_sync`; the file currently contains the active username/password pair.
- The only test today is `src/test/java/elghiati/studysync/StudysyncApplicationTests.java`, which just checks that the Spring context starts.
## Repository hygiene
- `target/`, IDE metadata, and wrapper artifacts are already ignored in `.gitignore`; do not commit generated build output.
- Keep new code aligned with the current package structure and naming, even where the codebase is still incomplete (for example, `UserService` is a constructor-injected shell without annotations yet).
- When adding new features, inspect nearby entities and DTOs first; this codebase already relies on validation-heavy DTOs and entity inheritance rather than ad hoc models.