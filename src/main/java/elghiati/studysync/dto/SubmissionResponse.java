package elghiati.studysync.dto;

import elghiati.studysync.enums.SubmissionStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record SubmissionResponse(
        UUID id,
        SubmissionStatus status,
        BigDecimal grade,
        String student,
        String taskTitle,
        String fileUrl,
        String comment,
        Instant submittedAt
) {
}
