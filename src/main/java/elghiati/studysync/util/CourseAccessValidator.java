package elghiati.studysync.util;

import elghiati.studysync.entity.Instructor;
import elghiati.studysync.entity.Student;
import elghiati.studysync.entity.User;
import elghiati.studysync.service.CourseService;
import elghiati.studysync.service.EnrollmentService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CourseAccessValidator {
        private final CourseService courseService;
        private final EnrollmentService enrollmentService;
        public CourseAccessValidator(CourseService courseService , EnrollmentService enrollmentService) {
            this.courseService = courseService;
            this.enrollmentService = enrollmentService;
        }
    
        public void validateCourseAccess(User user , UUID courseId) {
            if(user instanceof Student student) {
                enrollmentService.verifyEnrollment(student , courseId);
            } else if(user instanceof Instructor instructor) {
                courseService.verifyInstructorCourse(instructor , courseId);
            }
        }
}