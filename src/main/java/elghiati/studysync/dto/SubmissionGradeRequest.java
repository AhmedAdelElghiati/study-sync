package elghiati.studysync.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SubmissionGradeRequest(
    @NotNull(message = "Grade is required")
    @Min(value = 0, message = "Grade must be at least 0")
    BigDecimal grade
) {
}