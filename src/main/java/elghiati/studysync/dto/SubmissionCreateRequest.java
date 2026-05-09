package elghiati.studysync.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public record SubmissionCreateRequest(

        @NotNull(message = "File is required")
        MultipartFile file,

        @Size(max = 2000, message = "Comment must be at most 2000 characters")
        String comment
) {
}
