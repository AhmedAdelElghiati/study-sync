package elghiati.studysync.controller;

import elghiati.studysync.dto.EnrollmentResponse;
import elghiati.studysync.entity.Student;
import elghiati.studysync.entity.User;
import elghiati.studysync.service.EnrollmentService;
import elghiati.studysync.shared.APIResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;


@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {
    private final EnrollmentService enrollmentService;
    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }
    @PostMapping("/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<APIResponse<EnrollmentResponse>> enroll(
        @PathVariable UUID courseId,
        @AuthenticationPrincipal User user) {
        Student student = (Student) user;
        EnrollmentResponse enrollmentResponse = enrollmentService.enroll(student, courseId);
        return ResponseEntity
                .status(CREATED)
                .body(APIResponse.success(enrollmentResponse , "Enrolled successfully"));
    }

    @DeleteMapping("/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<APIResponse<Void>> drop(
        @PathVariable UUID courseId,
        @AuthenticationPrincipal User user) {
        Student student = (Student) user;
        enrollmentService.drop(student, courseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<APIResponse<Set<EnrollmentResponse>>> getEnrolledCourses(
        @AuthenticationPrincipal User user) {
        Student student = (Student) user;
        Set<EnrollmentResponse> enrollmentResponses = enrollmentService.getEnrolledCourses(student);
        return ResponseEntity.ok(APIResponse.success(enrollmentResponses , "Enrollment courses retrieved successfully"));
    }
}