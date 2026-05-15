package elghiati.studysync.dto;

import jakarta.validation.constraints.*;

import java.time.Instant;
import java.util.Set;

public record PollCreateRequest(

        @NotBlank(message = "Question is required")
        @Size(max = 255, message = "Question must be at most 255 characters")
        String question,

        @NotEmpty(message = "Options are required")
        @Size(min = 2, message = "At least 2 options are required")
        @Size(max = 10, message = "Maximum 10 options are allowed")
        Set<@NotEmpty String> options,

        @NotNull(message = "Is multi answer is required")
        Boolean isMultiAnswer,


        @NotNull(message = "Expires at is required")
        @Future(message = "Expires at must be in the future")
        Instant expiresAt


) {
}
