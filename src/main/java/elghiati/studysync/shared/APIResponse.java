package elghiati.studysync.shared;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class APIResponse<T> {
    private final boolean success;
    private final String message;
    private final T data;
    private final List<String> errors;
    private final Instant timestamp = Instant.now();
    private APIResponse(boolean success, T data, List<String> errors, String message) {
        this.success = success;
        this.data = data;
        this.errors = errors;
        this.message = message;
    }

    public static <T> APIResponse<T> success(T data, String message) {
        return new APIResponse<>(true, data, null, message);
    }

    public static <T> APIResponse<T> failure(List<String> errors, String message) {
        return new APIResponse<>(false, null, errors, message);
    }

}
