package elghiati.studysync.dto;

import java.time.Instant;
import java.util.UUID;

public record TaskResponse(
        UUID id,
        String title,
        String description,
        String courseName,
        String assignedBy,
        Instant createdAt,
        Instant deadline
) {
}
