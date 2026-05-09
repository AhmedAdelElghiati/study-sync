package elghiati.studysync.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SubmissionUpdateRequest(
        @NotBlank(message = "File URL is required")
        @Size(max = 2000, message = "File URL must not exceed 2000 characters")
        String fileUrl
) {
}
