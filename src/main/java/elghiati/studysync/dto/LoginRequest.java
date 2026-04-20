package elghiati.studysync.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequest(

        @NotBlank(message = "username is required")
        @Size(min = 4, max = 100, message = "User name must be between 4 and 100 characters")
        @Pattern(regexp = "^[A-Za-z0-9._-]+$", message = "User name can only contain letters, numbers, dots, underscores, and dashes")
        String username,

        @NotBlank(message = "Password is required")
        String password
) {}