package elghiati.studysync.dto;

import elghiati.studysync.enums.Department;
import elghiati.studysync.enums.Level;
import jakarta.validation.constraints.*;

public record StudentUpdateRequest(
     @NotBlank(message = "User name is required")
    @Size(min = 4, max = 100, message = "User name must be between 4 and 100 characters")
    @Pattern(regexp = "^[A-Za-z0-9._-]+$", message = "User name can only contain letters, numbers, dots, underscores, and dashes")
    String userName,
    @NotNull(message = "Department is required")
    Department department,
    @NotNull(message = "Level is required")
    Level level,
    @NotNull(message = "GPA is required")
     @DecimalMin(value = "0.0", message = "GPA must be at least 0.0")
     @DecimalMax(value = "4.0", message = "GPA must be at most 4.0")
    double gpa,
    @NotBlank(message = "Seat number is required")
    @Size(min = 1, max = 30, message = "Seat number must be between 1 and 30 characters")
    @Pattern(regexp = "^[0-9]+$", message = "Seat number can only contain numbers")
    String seatNumber
) {
}
