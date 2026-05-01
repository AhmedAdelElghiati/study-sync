package elghiati.studysync.repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import elghiati.studysync.entity.Enrollment;
import elghiati.studysync.enums.EnrollmentStatus;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
    boolean existsByStudentIdAndCourseId(UUID id, UUID courseId);
    Optional<Enrollment> findByStudentIdAndCourseId(UUID id, UUID courseId);

     Set<Enrollment> findByStudentIdAndStatus(UUID id, EnrollmentStatus status);
}
