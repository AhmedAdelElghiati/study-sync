package elghiati.studysync.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import elghiati.studysync.enums.Department;
import elghiati.studysync.enums.Level;
import elghiati.studysync.enums.Semester;

public record CourseResponse(
        UUID id,
        String name,
        String code,
        String description,
        Level level,
        Semester semester,
        Set<Department> departments,
        String professorName,
        Set<String> teachingAssistantNames,
        Instant createdAt
) {
}