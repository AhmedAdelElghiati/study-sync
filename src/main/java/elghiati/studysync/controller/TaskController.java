package elghiati.studysync.controller;

import elghiati.studysync.dto.TaskCreateRequest;
import elghiati.studysync.dto.TaskResponse;
import elghiati.studysync.dto.TaskStatsResponse;
import elghiati.studysync.dto.TaskUpdateRequest;
import elghiati.studysync.entity.Instructor;
import elghiati.studysync.entity.Student;
import elghiati.studysync.entity.User;
import elghiati.studysync.service.TaskService;
import elghiati.studysync.shared.APIResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api")
public class TaskController {
    private final TaskService taskService;
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/courses/{courseId}/tasks")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<APIResponse<TaskResponse>> createTask(
            @RequestBody @Valid TaskCreateRequest request,
            @PathVariable UUID courseId,
            @AuthenticationPrincipal User currentUser
    ) {
        Instructor instructor = (Instructor) currentUser;
        TaskResponse taskResponse = taskService.createTask(request, instructor, courseId);
        return ResponseEntity
                .status(CREATED)
                .body(APIResponse.success(taskResponse , "Task Created successfully"));
    }
    @PutMapping("/courses/{courseId}/tasks/{taskId}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<APIResponse<TaskResponse>> updateTask(
            @RequestBody @Valid TaskUpdateRequest request,
            @PathVariable UUID courseId,
            @PathVariable("taskId") UUID taskId,
            @AuthenticationPrincipal User currentUser
    ) {
        Instructor instructor = (Instructor) currentUser;
        TaskResponse taskResponse = taskService.updateTask(request, instructor, courseId, taskId);
        return ResponseEntity.ok(APIResponse.success(taskResponse , "Task updated successfully"));
    }
    @DeleteMapping("/courses/{courseId}/tasks/{taskId}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<Void> deleteTask(
            @PathVariable UUID courseId,
            @PathVariable("taskId") UUID taskId,
            @AuthenticationPrincipal User currentUser
    ) {
        Instructor instructor = (Instructor) currentUser;
        taskService.deleteTask(instructor, courseId, taskId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/courses/{courseId}/tasks")
    public ResponseEntity<APIResponse<List<TaskResponse>>> getTasksByCourseId(
            @PathVariable UUID courseId,
            @AuthenticationPrincipal User currentUser
    ) {
        List<TaskResponse> response = taskService.getTasksByCourseId(courseId, currentUser);
        return ResponseEntity.ok(APIResponse.success(response, "Tasks retrieved successfully"));
    }

    @GetMapping("/courses/{courseId}/tasks/unsubmitted")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<APIResponse<List<TaskResponse>>> getUnsubmittedTasksByCourseAndStudent(
            @PathVariable UUID courseId,
            @AuthenticationPrincipal User currentUser
    ) {
        Student student = (Student) currentUser;
        List<TaskResponse> response = taskService.getUnsubmittedTasksByCourseAndStudent(courseId, student);
        return ResponseEntity.ok(APIResponse.success(response, "Tasks retrieved successfully"));
    }
    @GetMapping("/tasks")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<APIResponse<List<TaskResponse>>> getUnsubmittedTasksByStudent(
            @AuthenticationPrincipal User currentUser
    ) {
        Student student = (Student) currentUser;
        List<TaskResponse> response = taskService.getUnsubmittedTasksByStudent(student);
        return ResponseEntity.ok(APIResponse.success(response, "Tasks retrieved successfully"));
    }
    @GetMapping("/tasks/stats")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<APIResponse<TaskStatsResponse>> getTaskStats(
            @AuthenticationPrincipal User currentUser
    ) {
        Student student = (Student) currentUser;
        TaskStatsResponse stats = taskService.getTaskStatsForStudent(student);
        return ResponseEntity.ok(APIResponse.success(stats, "Task stats retrieved successfully"));
    }

}
