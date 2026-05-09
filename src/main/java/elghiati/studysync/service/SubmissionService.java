package elghiati.studysync.service;

import elghiati.studysync.dto.SubmissionCreateRequest;
import elghiati.studysync.dto.SubmissionGradeRequest;
import elghiati.studysync.dto.SubmissionResponse;
import elghiati.studysync.entity.Instructor;
import elghiati.studysync.entity.Student;
import elghiati.studysync.entity.Submission;
import elghiati.studysync.entity.Task;
import elghiati.studysync.enums.SubmissionStatus;
import elghiati.studysync.exception.BusinessRuleException;
import elghiati.studysync.exception.ResourceNotFoundException;
import elghiati.studysync.repository.SubmissionRepository;
import elghiati.studysync.util.CourseAccessValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final CourseAccessValidator courseAccessValidator;
    private final TaskService taskService;
    private final CloudinaryService cloudinaryService;
    public SubmissionService(
            SubmissionRepository submissionRepository,
            CourseAccessValidator courseAccessValidator,
            TaskService taskService,
            CloudinaryService cloudinaryService
    ) {
        this.submissionRepository = submissionRepository;
        this.courseAccessValidator = courseAccessValidator;
        this.taskService = taskService;
        this.cloudinaryService = cloudinaryService;
    }

    private SubmissionResponse mapSubmissionToResponse(Submission submission) {
        return new SubmissionResponse(
                submission.getId(),
                submission.getStatus(),
                submission.getGrade(),
                submission.getStudent().getFullName(),
                submission.getTask().getTitle(),
                submission.getFileUrl(),
                submission.getComment(),
                submission.getSubmittedAt()
        );
    }

    @Transactional
    public SubmissionResponse saveSubmission(
            SubmissionCreateRequest request,
            Student student,
            UUID courseId,
            UUID taskId
    ) {
        courseAccessValidator.validateCourseAccess(student , courseId);
        Task task = taskService.getTaskById(taskId);

        if(!task.getCourse().getId().equals(courseId)) {
            throw new BusinessRuleException("Task does not belong to the specified course");
        }

        if(task.getDeadline().isBefore(Instant.now())) {
            throw new BusinessRuleException("The deadline for this task has passed");
        }

        if (submissionRepository.findByTaskIdAndStudentId(taskId, student.getId()).isPresent()) {
            throw new BusinessRuleException("You have already submitted for this task");
        }

        if(request.file() == null || request.file().isEmpty()) {
            throw new BusinessRuleException("File is required for file materials");
        }
        String fileUrl = cloudinaryService.upload(request.file());

        Submission submission = new Submission();
        submission.setStudent(student);
        submission.setTask(task);
        submission.setGrade(null);
        submission.setFileUrl(fileUrl);
        submission.setComment(request.comment());

        return mapSubmissionToResponse(submissionRepository.save(submission));
    }

    @Transactional(readOnly = true)
    public List<SubmissionResponse> getSubmissionsByTaskId(Instructor instructor , UUID taskId , UUID courseId) {
        courseAccessValidator.validateCourseAccess(instructor , courseId);
        return submissionRepository.findByTaskId(taskId).stream()
                .map(this::mapSubmissionToResponse)
                .toList();
    }
    @Transactional(readOnly = true)
    public List<SubmissionResponse> getSubmissionsByStudentId(Instructor instructor , UUID studentId , UUID courseId) {
        courseAccessValidator.validateCourseAccess(instructor , courseId);
        return submissionRepository.findByStudentId(studentId).stream()
                .map(this::mapSubmissionToResponse)
                .toList();
    }
    @Transactional(readOnly = true)
    public List<SubmissionResponse> getSubmissionsByStudentAndCourseId(Student student , UUID courseId) {
        courseAccessValidator.validateCourseAccess(student , courseId);
        return submissionRepository.findByCourseIdAndStudentId(courseId, student.getId()).stream()
                .map(this::mapSubmissionToResponse)
                .toList();
    }

    @Transactional
    public SubmissionResponse gradeSubmission(Instructor instructor , UUID submissionId , UUID courseId , SubmissionGradeRequest gradeRequest) {
        courseAccessValidator.validateCourseAccess(instructor , courseId);
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission with id " + submissionId + " not found"));
        
        if(!submission.getTask().getCourse().getId().equals(courseId)) {
            throw new BusinessRuleException("Submission does not belong to the specified course");
        }

        if(gradeRequest.grade().compareTo(submission.getTask().getMaxGrade()) > 0) {
            throw new BusinessRuleException("Grade cannot exceed the maximum grade for the task");
        }

        submission.setGrade(gradeRequest.grade());
        submission.setStatus(SubmissionStatus.GRADED);
        return mapSubmissionToResponse(submissionRepository.save(submission));
    }
    @Transactional
    public void deleteSubmission(Student student , UUID courseId , UUID submissionId) {
        courseAccessValidator.validateCourseAccess(student , courseId);
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission with id " + submissionId + " not found"));
        
        if(!submission.getTask().getCourse().getId().equals(courseId)) {
            throw new BusinessRuleException("Submission does not belong to the specified course");
        }
        if(!submission.getStudent().getId().equals(student.getId())) {
            throw new AccessDeniedException("You can only delete your own submissions");
        }
        if(submission.getTask().getDeadline().isBefore(Instant.now())) {
            throw new BusinessRuleException("You cannot delete a submission after the deadline has passed");
        }
        if(submission.getStatus() == SubmissionStatus.GRADED) {
            throw new BusinessRuleException("You cannot delete a graded submission");
        }
        submissionRepository.delete(submission);
        try {
            cloudinaryService.delete(submission.getFileUrl());
        } catch (Exception e) {
            log.error("Error deleting file from Cloudinary: " + e.getMessage());
        }
    }
}