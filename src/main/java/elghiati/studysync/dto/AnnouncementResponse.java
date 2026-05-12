package elghiati.studysync.dto;

import elghiati.studysync.enums.Role;

import java.time.Instant;
import java.util.UUID;

public record AnnouncementResponse(
        UUID id,
        String title,
        String content,
        String courseName,
        String senderName,
        String senderRole,
        Instant createdAt
) {
}
