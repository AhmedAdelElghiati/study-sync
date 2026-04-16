package elghiati.studysync.dto;

import elghiati.studysync.enums.Department;
import elghiati.studysync.enums.InstructorType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record InstructorCreateRequest(
        @NotBlank(message = "Full name is required")
        @Size(min = 10, max = 100, message = "Full name must be between 10 and 100 characters")
        String fullName,

        @NotBlank(message = "User name is required")
        @Size(min = 4, max = 100, message = "User name must be between 4 and 100 characters")
        @Pattern(regexp = "^[A-Za-z0-9._-]+$", message = "User name can only contain letters, numbers, dots, underscores, and dashes")
        String userName,

        @NotBlank(message = "University email is required")
        @Email(message = "University email must be a valid email address")
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@ics\\.tanta\\.edu\\.eg$",
                message = "You must register with a valid university email address (e.g., @ics.tanta.edu.eg)")
        String universityEmail,

        @NotNull(message = "Department is required")
        Department department,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
                message = "Password must include uppercase, lowercase, number, and special character"
        )
        String password,

        @NotBlank(message = "ID card path is required")
        String idCardPath,

        @NotNull(message = "Instructor type is required")
        InstructorType instructorType
) {
}