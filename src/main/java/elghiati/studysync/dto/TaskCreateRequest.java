package elghiati.studysync.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record TaskCreateRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title must not exceed 255 characters")
        String title,

        @Size(max = 4000, message = "Description must not exceed 4000 characters")
        String description,

        @NotNull(message = "Deadline is required")
        @Future(message = "Deadline must be in the future")
        Instant deadline
) {
}
