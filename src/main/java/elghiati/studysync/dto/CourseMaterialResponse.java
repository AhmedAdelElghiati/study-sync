package elghiati.studysync.dto;

import elghiati.studysync.enums.MaterialType;

import java.time.Instant;
import java.util.UUID;

public record CourseMaterialResponse(
		UUID id,
		String title,
		MaterialType type,
		String uploadedByName,
		String url,
		Instant createdAt
) {
}
