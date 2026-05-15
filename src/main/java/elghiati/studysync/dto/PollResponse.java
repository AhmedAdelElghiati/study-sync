package elghiati.studysync.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PollResponse(
        UUID id,
        String question,
        Instant expiresAt,
        boolean isMultiAnswer,
        boolean isVoted,
        long totalVotes,
        List<PollOptionResponse> options,
        String createdBy,
        String courseName,
        String creatorRole
) {
}
