package elghiati.studysync.service;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import elghiati.studysync.dto.CourseResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import elghiati.studysync.dto.EnrollmentResponse;
import elghiati.studysync.entity.Course;
import elghiati.studysync.entity.Enrollment;
import elghiati.studysync.entity.Student;
import elghiati.studysync.enums.EnrollmentStatus;
import elghiati.studysync.exception.BusinessRuleException;
import elghiati.studysync.exception.DuplicateResourceException;
import elghiati.studysync.repository.EnrollmentRepository;

@Service
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseService courseService;

    public EnrollmentService(EnrollmentRepository enrollmentRepository , CourseService courseService) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseService = courseService;
    }

    private EnrollmentResponse mapEnrollmentToResponse(Enrollment enrollment) {
        return new EnrollmentResponse(
                enrollment.getId(),
                enrollment.getCourse().getId(),
                enrollment.getCourse().getName(),
                enrollment.getCourse().getCode(),
                enrollment.getCourse().getProfessor().getFullName(),
                enrollment.getCourse().getTeachingAssistants().stream().map(ta -> ta.getFullName()).collect(Collectors.toSet()),
                enrollment.getStatus(),
                enrollment.getEnrolledAt()
        );
    }

    @Transactional
    public EnrollmentResponse enroll(Student student , UUID courseId) {
        Optional<Enrollment> existingEnrollment = enrollmentRepository.findByStudentIdAndCourseId(student.getId(), courseId);
        if (existingEnrollment.isPresent()) {
            if (existingEnrollment.get().getStatus() == EnrollmentStatus.ACTIVE) {
                throw new DuplicateResourceException("Student is already enrolled in this course");
            } else if (existingEnrollment.get().getStatus() == EnrollmentStatus.DROPPED) {
                existingEnrollment.get().setStatus(EnrollmentStatus.ACTIVE);
                existingEnrollment.get().setReEnrolledAt(Instant.now());
                return mapEnrollmentToResponse(enrollmentRepository.save(existingEnrollment.get()));
            }
        }

        Course course = courseService.findById(courseId);

        if(student.getLevel() != course.getLevel()) {
            throw new BusinessRuleException("Student level does not match course level");
        }
        if(!course.getDepartments().isEmpty() && !course.getDepartments().contains(student.getDepartment())) {
            throw new BusinessRuleException("Student department does not match course department");
        }
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        return mapEnrollmentToResponse(enrollmentRepository.save(enrollment));
    }

    @Transactional
    public void drop(Student student , UUID courseId) {
        Course course = courseService.findById(courseId);
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(student.getId(), course.getId())
                .orElseThrow(() -> new BusinessRuleException("Student is not enrolled in this course"));
        enrollment.setStatus(EnrollmentStatus.DROPPED);
        enrollmentRepository.save(enrollment);
    }
    @Transactional(readOnly = true)
    public Set<EnrollmentResponse> getEnrolledCourses(Student student) {
        return enrollmentRepository.findByStudentIdAndStatus(student.getId(), EnrollmentStatus.ACTIVE)
                .stream()
                .map(this::mapEnrollmentToResponse)
                .collect(Collectors.toSet());
    }
    
    public void verifyEnrollment(Student student , UUID courseId) {
        Optional<Enrollment> enrollment = enrollmentRepository.findByStudentIdAndCourseId(student.getId(), courseId);
        if(enrollment.isEmpty() || enrollment.get().getStatus() != EnrollmentStatus.ACTIVE) {
            throw new BusinessRuleException("Student is not enrolled in this course");
        }
    }
}
