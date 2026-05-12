package elghiati.studysync.repository;

import elghiati.studysync.entity.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    @EntityGraph(attributePaths = {"course" , "assignedBy" })
    List<Task> findByCourseId(UUID courseId);

    @Query("""
    SELECT t FROM Task t
    JOIN FETCH t.course
    JOIN FETCH t.assignedBy
    WHERE t.course.id = :courseId 
    AND NOT EXISTS 
        (SELECT s FROM Submission s 
         WHERE s.task = t 
         AND s.student.id = :studentId
          )
    """)
    List<Task> findUnsubmittedTasksByCourseAndStudent(@Param("courseId") UUID courseId, @Param("studentId") UUID studentId);
}
