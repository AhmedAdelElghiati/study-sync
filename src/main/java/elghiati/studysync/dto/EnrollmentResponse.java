package elghiati.studysync.dto;

import elghiati.studysync.enums.EnrollmentStatus;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record EnrollmentResponse(
        UUID id,
        UUID courseId,
        String courseName,
        String courseCode,
        String professorName,
        Set<String> teachingAssistantNames,
        EnrollmentStatus status,
        Instant enrolledAt
) {

}
