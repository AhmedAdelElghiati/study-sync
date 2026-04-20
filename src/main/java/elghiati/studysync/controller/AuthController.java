package elghiati.studysync.controller;

import elghiati.studysync.dto.AuthResponse;
import elghiati.studysync.dto.InstructorCreateRequest;
import elghiati.studysync.dto.LoginRequest;
import elghiati.studysync.dto.StudentCreateRequest;
import elghiati.studysync.service.AuthService;
import elghiati.studysync.shared.APIResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register/student")
    public ResponseEntity<APIResponse<AuthResponse>> register(@RequestBody @Valid StudentCreateRequest request) {
        AuthResponse authResponse = authService.registerStudent(request);
         return ResponseEntity.status(CREATED)
                 .body(APIResponse.success(authResponse, "Student registered successfully"));
    }
    @PostMapping("/register/instructor")
    public ResponseEntity<APIResponse<AuthResponse>> register(@RequestBody @Valid InstructorCreateRequest request) {
        AuthResponse authResponse = authService.registerInstructor(request);
         return ResponseEntity.status(CREATED)
                 .body(APIResponse.success(authResponse, "Instructor registered successfully"));
    }
    @PostMapping("/login")
    public ResponseEntity<APIResponse<AuthResponse>> login(@RequestBody @Valid LoginRequest request){
        AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(APIResponse.success(authResponse, "Login successful"));
    }
}
