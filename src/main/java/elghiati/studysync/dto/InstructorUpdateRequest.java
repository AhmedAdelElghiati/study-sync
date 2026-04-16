package elghiati.studysync.dto;

import elghiati.studysync.enums.Department;
import elghiati.studysync.enums.InstructorType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record InstructorUpdateRequest(
    
        @NotBlank(message = "User name is required")
        @Size(min = 4, max = 100, message = "User name must be between 4 and 100 characters")
        @Pattern(regexp = "^[A-Za-z0-9._-]+$", message = "User name can only contain letters, numbers, dots, underscores, and dashes")
        String userName,
        @NotNull(message = "Department is required")
        Department department,
        @NotBlank(message = "ID card path is required")
        String idCardPath,
        @NotNull(message = "Instructor type is required")
        InstructorType instructorType
) {
}