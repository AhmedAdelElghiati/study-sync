package elghiati.studysync.controller;

import elghiati.studysync.dto.TaskCreateRequest;
import elghiati.studysync.dto.TaskResponse;
import elghiati.studysync.dto.TaskUpdateRequest;
import elghiati.studysync.entity.Instructor;
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
@RequestMapping("/api/courses/{courseId}/tasks")
public class TaskController {
    private final TaskService taskService;
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
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
    @PutMapping("/{taskId}")
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
    @DeleteMapping("/{taskId}")
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
    @GetMapping
    public ResponseEntity<APIResponse<List<TaskResponse>>> getTasksByCourseId(
            @PathVariable UUID courseId,
            @AuthenticationPrincipal User currentUser
    ) {
        List<TaskResponse> response = taskService.getTasksByCourseId(courseId, currentUser);
        return ResponseEntity.ok(APIResponse.success(response, "Tasks retrieved successfully"));
    }
}
