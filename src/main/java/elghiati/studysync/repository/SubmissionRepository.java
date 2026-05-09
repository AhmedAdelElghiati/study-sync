package elghiati.studysync.repository;

import elghiati.studysync.entity.Submission;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, UUID> {
    @EntityGraph(attributePaths = {"task" , "student" })
    Optional<Submission> findByTaskIdAndStudentId(UUID taskId, UUID studentId);
    @EntityGraph(attributePaths = {"task" , "student" })
    List<Submission> findByTaskId(UUID taskId);
    @EntityGraph(attributePaths = {"task" , "student" })
    List<Submission> findByStudentId(UUID studentId);
    @Query("""
    SELECT s FROM Submission s 
        JOIN FETCH s.task t 
        JOIN FETCH s.student 
        WHERE t.course.id = :courseId 
        AND s.student.id = :studentId
    """)
    List<Submission> findByCourseIdAndStudentId(@Param("courseId") UUID courseId, @Param("studentId") UUID studentId);
}
