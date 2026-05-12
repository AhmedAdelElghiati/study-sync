package elghiati.studysync.service;

import elghiati.studysync.dto.TaskCreateRequest;
import elghiati.studysync.dto.TaskResponse;
import elghiati.studysync.dto.TaskStatsResponse;
import elghiati.studysync.dto.TaskUpdateRequest;
import elghiati.studysync.entity.*;
import elghiati.studysync.exception.BusinessRuleException;
import elghiati.studysync.exception.ResourceNotFoundException;
import elghiati.studysync.repository.TaskRepository;
import elghiati.studysync.util.CourseAccessValidator;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final CourseAccessValidator courseAccessValidator;
    private final CourseService courseService;

    public TaskService(
            TaskRepository taskRepository,
            CourseAccessValidator courseAccessValidator,
            CourseService courseService
    ) {
        this.taskRepository = taskRepository;
        this.courseAccessValidator = courseAccessValidator;
        this.courseService = courseService;
    }

    private TaskResponse mapToTaskResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getCourse().getName(),
                task.getAssignedBy().getFullName(),
                task.getMaxGrade(),
                task.getCreatedAt(),
                task.getDeadline()
        );
    }
    public Task getTaskById(UUID taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + taskId + " not found"));
    }
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByCourseId(UUID courseId , User currentUser) {
        courseAccessValidator.validateCourseAccess(currentUser , courseId);
        return taskRepository.findByCourseId(courseId).stream()
                .map(this::mapToTaskResponse)
                .toList();
    }
    @Transactional(readOnly = true)
    public List<TaskResponse> getUnsubmittedTasksByCourseAndStudent(UUID courseId , Student student) {
        courseAccessValidator.validateCourseAccess(student , courseId);
        return taskRepository.findUnsubmittedTasksByCourseAndStudent(courseId , student.getId()).stream()
                .map(this::mapToTaskResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getUnsubmittedTasksByStudent(Student student) {
        return taskRepository.findUnsubmittedTasksByStudent(student.getId()).stream()
                .map(this::mapToTaskResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskStatsResponse getTaskStatsForStudent(Student student) {
        long unsubmittedTasks = taskRepository.countUnsubmittedTasksByStudent(student.getId());
        long submittedTasks = taskRepository.countSubmittedTasksByStudent(student.getId());
        long totalTasks = unsubmittedTasks + submittedTasks;
        return new TaskStatsResponse(unsubmittedTasks, submittedTasks , totalTasks);
    }


    @Transactional
    public TaskResponse createTask(TaskCreateRequest request, Instructor currentUser , UUID courseId) {
        courseAccessValidator.validateCourseAccess(currentUser , courseId);
        Course course = courseService.findById(courseId);

        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setCourse(course);
        task.setMaxGrade(request.maxGrade());
        task.setAssignedBy(currentUser);
        task.setDeadline(request.deadline());
        return mapToTaskResponse(taskRepository.save(task));
    }
    @Transactional
    public TaskResponse updateTask (TaskUpdateRequest request, Instructor currentUser , UUID courseId , UUID taskId) {
        courseAccessValidator.validateCourseAccess(currentUser , courseId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id:" + taskId + " not found"));

        if (!task.getCourse().getId().equals(courseId)) {
            throw new BusinessRuleException("Task does not belong to this course");
        }

        if (!task.getAssignedBy().getId().equals(currentUser.getId())) {
            throw new BusinessRuleException("You can only update tasks you assigned");
        }
        task.setTitle(request.title());
        task.setMaxGrade(request.maxGrade());
        task.setDescription(request.description());
        task.setDeadline(request.deadline());
        return mapToTaskResponse(taskRepository.save(task));
    }
    @Transactional
    public void deleteTask (Instructor currentUser , UUID courseId , UUID taskId) {
        courseAccessValidator.validateCourseAccess(currentUser , courseId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id:" + taskId + " not found"));

        if (!task.getCourse().getId().equals(courseId)) {
            throw new BusinessRuleException("Task does not belong to this course");
        }

        if (!task.getAssignedBy().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You can only delete tasks you assigned");
        }
        taskRepository.delete(task);
    }


}
