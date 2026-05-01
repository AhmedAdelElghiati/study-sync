package elghiati.studysync.service;

import elghiati.studysync.dto.CourseCreateRequest;
import elghiati.studysync.dto.CourseResponse;
import elghiati.studysync.dto.CourseUpdateRequest;
import elghiati.studysync.entity.Course;
import elghiati.studysync.entity.Instructor;
import elghiati.studysync.entity.Student;
import elghiati.studysync.exception.DuplicateResourceException;
import elghiati.studysync.exception.ResourceNotFoundException;
import elghiati.studysync.repository.CourseRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final InstructorService instructorService;

    public CourseService(
        CourseRepository courseRepository,
        InstructorService instructorService
         ) {
        this.courseRepository = courseRepository;
        this.instructorService = instructorService;
    }

    private CourseResponse mapToCourseResponse(Course course) {
        Set<String> taNames = course.getTeachingAssistants()
        .stream()
        .map(Instructor::getFullName)
        .collect(Collectors.toSet());
        return new CourseResponse(
                course.getId(),
                course.getName(),
                course.getCode(),
                course.getDescription(),
                course.getLevel(),
                course.getSemester(),
                course.getDepartments(),
                course.getProfessor().getFullName(),
                taNames,
                course.getCreatedAt()
        );
    }

    public Course findById(UUID id) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course with id: " + id + " not found"));
        return course;
    }

    public CourseResponse getCourseById(UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course with id: " + courseId + " not found"));
        return mapToCourseResponse(course);
    }

    @Transactional
    public CourseResponse createCourse(CourseCreateRequest request ,Instructor professor) {
        if (courseRepository.existsByCode(request.code())) {
            throw new DuplicateResourceException("Course with code: " + request.code() + " already exists");
        }
        Set<Instructor> instructors = new HashSet<>();
        if(request.teachingAssistantIds() != null && !request.teachingAssistantIds().isEmpty()){
           instructors = instructorService.getInstructorsByIds(request.teachingAssistantIds());
        }
        Course course = new Course();
        course.setName(request.name());
        course.setCode(request.code());
        course.setDescription(request.description());
        course.setLevel(request.level());
        course.setSemester(request.semester());
        course.setDepartments(request.departments() != null ? request.departments() : new HashSet<>());
        course.setProfessor(professor);
        course.setTeachingAssistants(instructors);
        Course savedCourse = courseRepository.save(course);
        return mapToCourseResponse(savedCourse);
    }
    @Transactional(readOnly = true)
    public List<CourseResponse> getCoursesForStudent(Student student) {
        List<Course> courses = courseRepository.findCoursesForStudent(student.getLevel(), student.getDepartment());
        return courses.stream().map(this::mapToCourseResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getCoursesForInstructor(Instructor instructor) {
        List<Course> courses = courseRepository.findByInstructor(instructor);
        return courses.stream().map(this::mapToCourseResponse).collect(Collectors.toList());
    }

    @Transactional
    public CourseResponse updateCourse(UUID courseId, CourseUpdateRequest request , Instructor professor) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course with id: " + courseId + " not found"));
        if (!course.getProfessor().getId().equals(professor.getId())) {
            throw new AccessDeniedException("You can only modify your own courses");
        }
        Set<Instructor> instructors = new HashSet<>();
        if (request.teachingAssistantIds() != null && !request.teachingAssistantIds().isEmpty()) {
            instructors = instructorService.getInstructorsByIds(request.teachingAssistantIds());
        }

        course.setName(request.name());
        course.setDescription(request.description());
        course.setLevel(request.level());
        course.setSemester(request.semester());
        course.setDepartments(request.departments() != null ? request.departments() : new HashSet<>());
        course.setTeachingAssistants(instructors);

        Course updatedCourse = courseRepository.save(course);
        return mapToCourseResponse(updatedCourse);
    }
    
    public void deleteCourse(UUID courseId , Instructor professor) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course with id: " + courseId + " not found"));
        if (!course.getProfessor().getId().equals(professor.getId())) {
            throw new AccessDeniedException("You can only modify your own courses");
        }

        courseRepository.delete(course);
    }

    public void verifyInstructorCourse(Instructor instructor , UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course with id: " + courseId + " not found"));
        if (!course.getProfessor().getId().equals(instructor.getId()) && !course.getTeachingAssistants().contains(instructor)) {
            throw new AccessDeniedException("You can only access your own courses");
        }
    }
}