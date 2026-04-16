package elghiati.studysync.dto;

import elghiati.studysync.enums.ApprovalStatus;
import elghiati.studysync.enums.Department;
import elghiati.studysync.enums.Level;

import java.time.Instant;
import java.util.UUID;

public record StudentResponse(
    UUID id,
    String fullName,
    String userName,
    String universityEmail,
    Department department,
    Level level,
    String seatNumber,
    String idCardPath,
    ApprovalStatus approvalStatus,
    Instant createdAt
) {
}
