package elghiati.studysync.controller;

import elghiati.studysync.dto.CourseCreateRequest;
import elghiati.studysync.dto.CourseResponse;
import elghiati.studysync.dto.CourseUpdateRequest;
import elghiati.studysync.entity.Instructor;
import elghiati.studysync.entity.Student;
import elghiati.studysync.entity.User;
import elghiati.studysync.service.CourseService;
import elghiati.studysync.service.EnrollmentService;
import elghiati.studysync.shared.APIResponse;
import elghiati.studysync.util.CourseAccessValidator;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final CourseAccessValidator courseAccessValidator;
    public CourseController(CourseService courseService , EnrollmentService enrollmentService , CourseAccessValidator courseAccessValidator) {
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
        this.courseAccessValidator = courseAccessValidator;
    }
    @PostMapping
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<APIResponse<CourseResponse>> createCourse(
            @RequestBody @Valid CourseCreateRequest courseCreateRequest,
            @AuthenticationPrincipal User currentUser
    ) {
        Instructor professor = (Instructor) currentUser;
        CourseResponse courseResponse = courseService.createCourse(courseCreateRequest, professor);
        return ResponseEntity.status(CREATED)
                .body(APIResponse.success(courseResponse , "Course created successfully"));
    }


    @PutMapping("/{courseId}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<APIResponse<CourseResponse>> updateCourse(
            @PathVariable UUID courseId,
            @RequestBody @Valid CourseUpdateRequest courseUpdateRequest,
            @AuthenticationPrincipal User currentUser
    ) {
        Instructor professor = (Instructor) currentUser;
        CourseResponse courseResponse = courseService.updateCourse( courseId,courseUpdateRequest , professor);
        return ResponseEntity.ok(APIResponse.success(courseResponse , "Course updated successfully"));
    }

    @DeleteMapping("/{courseId}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<APIResponse<Void>> deleteCourse(
            @PathVariable UUID courseId,
            @AuthenticationPrincipal User currentUser
    ) {
        Instructor professor = (Instructor) currentUser;
        courseService.deleteCourse( courseId, professor);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<APIResponse<List<CourseResponse>>> getCoursesForStudent(
            @AuthenticationPrincipal User currentUser
    ) {
        Student student = (Student) currentUser;
        List<CourseResponse> courseResponse = courseService.getCoursesForStudent(student);
        return ResponseEntity.ok(APIResponse.success(courseResponse , "Courses retrieved successfully"));
    }

    @GetMapping(("/my-courses"))
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<APIResponse<List<CourseResponse>>> getCoursesForInstructor(
            @AuthenticationPrincipal User currentUser
    ) {
        Instructor instructor = (Instructor) currentUser;
        List<CourseResponse> courseResponse = courseService.getCoursesForInstructor(instructor);
        return ResponseEntity.ok(APIResponse.success(courseResponse , "Courses retrieved successfully"));
    }
    @GetMapping("/{courseId}")
    public ResponseEntity<APIResponse<CourseResponse>> getCourse(
            @PathVariable UUID courseId,
            @AuthenticationPrincipal User currentUser
    ) {
        courseAccessValidator.validateCourseAccess(currentUser , courseId);
        CourseResponse courseResponse = courseService.getCourseById(courseId);
        return ResponseEntity.ok(APIResponse.success(courseResponse , "Course retrieved successfully"));
    }
}
