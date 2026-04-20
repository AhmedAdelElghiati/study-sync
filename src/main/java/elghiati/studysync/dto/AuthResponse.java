package elghiati.studysync.dto;

public record AuthResponse(
        String token,
        String role,
        String userId
) {}

