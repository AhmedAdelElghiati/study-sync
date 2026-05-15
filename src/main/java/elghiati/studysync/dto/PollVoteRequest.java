package elghiati.studysync.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PollVoteRequest(
        @NotNull(message = "Option ID is required")
        UUID optionId
) {
}
