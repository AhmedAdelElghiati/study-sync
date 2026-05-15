package elghiati.studysync.dto;

import java.util.UUID;

public record PollOptionResponse(
        UUID id,
        String optionText,
        long votesCount,
        boolean isSelected
) {
}
