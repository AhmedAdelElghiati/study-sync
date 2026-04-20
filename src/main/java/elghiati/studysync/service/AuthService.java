package elghiati.studysync.service;

import elghiati.studysync.dto.*;
import elghiati.studysync.entity.Instructor;
import elghiati.studysync.entity.Student;
import elghiati.studysync.entity.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final StudentService studentService;
    private final InstructorService instructorService;
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    public AuthService(
            StudentService studentService,
            InstructorService instructorService,
            UserService userService,
            JwtService jwtService,
            AuthenticationManager authenticationManager
            ) {
        this.studentService = studentService;
        this.instructorService = instructorService;
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }
    public AuthResponse registerStudent(StudentCreateRequest request) {
        StudentResponse student = studentService.createStudent(request);
        User user = userService.findByUserName(request.userName()).get();
        String jwtToken = jwtService.generateToken(user);
        AuthResponse authResponse = new AuthResponse(
                jwtToken,
                user.getRole().name(),
                user.getId().toString()
        );
        return authResponse;
    }
    public AuthResponse registerInstructor(InstructorCreateRequest request) {
        InstructorResponse instructor = instructorService.createInstructor(request);
        User user = userService.findByUserName(request.userName()).get();
        String jwtToken = jwtService.generateToken(user);
        AuthResponse authResponse = new AuthResponse(
                jwtToken,
                user.getRole().name(),
                user.getId().toString()
        );
        return authResponse;
    }
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                        loginRequest.username(),
                        loginRequest.password()
                )
        );
        User user = (User) authentication.getPrincipal();
        String jwtToken = jwtService.generateToken(user);
        AuthResponse authResponse = new AuthResponse(
                jwtToken,
                user.getRole().name(),
                user.getId().toString()
        );
        return authResponse;
    }
}
