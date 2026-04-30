package elghiati.studysync.dto;

import java.util.Set;
import java.util.UUID;

import elghiati.studysync.enums.Department;
import elghiati.studysync.enums.Level;
import elghiati.studysync.enums.Semester;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CourseCreateRequest(
        @NotBlank(message = "Course name is required")
        @Size(max = 255, message = "Course name must be at most 255 characters")
        String name,

        @NotBlank(message = "Course code is required")
        @Size(max = 50, message = "Course code must be at most 50 characters")
        String code,

        @Size(max = 2000, message = "Course description must be at most 2000 characters")
        String description,

        @NotNull(message = "Level is required")
        Level level,

        @NotNull(message = "Semester is required")
        Semester semester,

        Set<Department> departments,

        Set<UUID> teachingAssistantIds
) {
}