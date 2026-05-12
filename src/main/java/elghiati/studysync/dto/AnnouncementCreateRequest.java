package elghiati.studysync.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AnnouncementCreateRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title must be at most 255 characters")
        String title,

        @NotBlank(message = "Content is required")
        @Size(max = 2000, message = "Content must be at most 2000 characters")
        String content
) {
}
