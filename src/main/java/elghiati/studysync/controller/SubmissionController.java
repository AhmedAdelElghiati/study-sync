package elghiati.studysync.controller;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import elghiati.studysync.dto.SubmissionCreateRequest;
import elghiati.studysync.dto.SubmissionGradeRequest;
import elghiati.studysync.dto.SubmissionResponse;
import elghiati.studysync.entity.Instructor;
import elghiati.studysync.entity.Student;
import elghiati.studysync.entity.User;
import elghiati.studysync.service.SubmissionService;
import elghiati.studysync.shared.APIResponse;
import io.swagger.v3.oas.annotations.Operation;


@RestController
@RequestMapping("/api/courses/{courseId}")
public class SubmissionController {
    private final SubmissionService submissionService;
    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }
    
    @Operation(summary = "Get my task submissions in a course" , description = "students can view all their submissions for tasks in a specific course")
    @GetMapping("/my-submissions")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<APIResponse<List<SubmissionResponse>>> getMySubmissionsInCourse(
        @AuthenticationPrincipal User currentUser,
        @PathVariable UUID courseId
    ) {
        Student student = (Student) currentUser;
        List<SubmissionResponse> submissions = submissionService.getSubmissionsByStudentAndCourseId(student, courseId);
        return ResponseEntity.ok(APIResponse.success(submissions, "Submissions retrieved successfully"));
    }
    
    
    @Operation(summary = "Submit a task" , description = "students can submit their work for a specific task in a course")
    @PostMapping("/tasks/{taskId}/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<APIResponse<SubmissionResponse>> saveSubmission(
           @ModelAttribute SubmissionCreateRequest request,
           @AuthenticationPrincipal User currentUser,
           @PathVariable UUID courseId,
           @PathVariable UUID taskId
    ) {
        Student student = (Student) currentUser;
        SubmissionResponse response = submissionService.saveSubmission(request, student, courseId, taskId);
        return ResponseEntity
        .status(CREATED)
        .body(APIResponse.success(response , "Task submitted successfully"));
    }

    @Operation(summary = "Delete a submission" , description = "students can delete their own submissions for a specific task in a course")
    @DeleteMapping("/submissions/{submissionId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<APIResponse<Void>> deleteSubmission(
        @AuthenticationPrincipal User currentUser,
        @PathVariable UUID submissionId,
        @PathVariable UUID courseId
    ) {
        Student student = (Student) currentUser;
        submissionService.deleteSubmission(student, courseId, submissionId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Grade a submission" , description = "Instructor can grade a student submission for a task")
    @PutMapping("/submissions/{submissionId}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<APIResponse<SubmissionResponse>> gradeSubmission(
        @AuthenticationPrincipal User currentUser,
        @PathVariable UUID submissionId,
        @PathVariable UUID courseId,
        @RequestBody SubmissionGradeRequest gradeRequest
    ) {
        Instructor instructor = (Instructor) currentUser;
        SubmissionResponse response = submissionService.gradeSubmission(instructor, submissionId, courseId, gradeRequest);
        return ResponseEntity.ok(APIResponse.success(response, "Submission graded successfully"));
    }

    @Operation(summary = "Get submissions for a task" , description = "Instructor can view all submissions for a specific task in a course")
    @GetMapping("/tasks/{taskId}/submissions")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<APIResponse<List<SubmissionResponse>>> getSubmissionsByTaskId(
        @AuthenticationPrincipal User currentUser,
        @PathVariable UUID taskId,
        @PathVariable UUID courseId
    ) {
        Instructor instructor = (Instructor) currentUser;
        List<SubmissionResponse> submissions = submissionService.getSubmissionsByTaskId(instructor, taskId, courseId);
        return ResponseEntity.ok(APIResponse.success(submissions, "Submissions retrieved successfully"));
    }

    @Operation(summary = "Get submissions for a student" , description = "Instructor can view all submissions for a specific student in a course")
    @GetMapping("/students/{studentId}/submissions")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<APIResponse<List<SubmissionResponse>>> getSubmissionsByStudentId(
        @AuthenticationPrincipal User currentUser,
        @PathVariable UUID studentId,
        @PathVariable UUID courseId
    ) {
        Instructor instructor = (Instructor) currentUser;
        List<SubmissionResponse> submissions = submissionService.getSubmissionsByStudentId(instructor, studentId, courseId);
        return ResponseEntity.ok(APIResponse.success(submissions, "Submissions retrieved successfully"));
    }

}