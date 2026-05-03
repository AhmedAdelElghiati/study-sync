package elghiati.studysync.dto;

import elghiati.studysync.enums.MaterialType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public record CourseMaterialCreateRequest(
	@NotBlank(message = "Title is required")
	@Size(max = 255, message = "Title must be at most 255 characters")
	String title,

	@NotNull(message = "Material type is required")
	MaterialType type,

	@Size(max = 2000, message = "URL/file path must be at most 2000 characters")
	String url,

    MultipartFile file

) {
}
