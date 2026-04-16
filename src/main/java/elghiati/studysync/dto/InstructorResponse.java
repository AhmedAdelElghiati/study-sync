package elghiati.studysync.dto;

import java.time.Instant;
import java.util.UUID;

import elghiati.studysync.enums.ApprovalStatus;
import elghiati.studysync.enums.Department;
import elghiati.studysync.enums.InstructorType;

public record InstructorResponse(
        UUID id,
        String fullName,
        String userName,
        String universityEmail,
        Department department,
        InstructorType instructorType,
        String idCardPath,
        ApprovalStatus approvalStatus,
        Instant createdAt
) {
}