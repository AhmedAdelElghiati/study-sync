package elghiati.studysync.repository;

import elghiati.studysync.entity.Poll;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PollRepository extends JpaRepository<Poll, UUID> {

    @EntityGraph(attributePaths = {"course" ,"createdBy" , "options"})
    List<Poll> findByCourseId(UUID courseId);

    @EntityGraph(attributePaths = {"course" ,"createdBy" , "options"})
    Optional<Poll> findById(UUID id);

    @Query("""
    SELECT DISTINCT p FROM Poll p
    JOIN FETCH p.course c
    JOIN FETCH p.createdBy
    JOIN FETCH p.options o   
    WHERE EXISTS (
        SELECT e FROM Enrollment e 
        WHERE e.student.id = :studentId 
        AND e.course.id = p.course.id
    )
    """)
    List<Poll> findPollsByStudentEnrollments(UUID studentId);

    @Query("""
    SELECT p.id FROM Poll p
    WHERE EXISTS (
        SELECT e FROM Enrollment e 
        WHERE e.student.id = :studentId 
        AND e.course.id = p.course.id
    )
    AND NOT EXISTS (
        SELECT r FROM PollVote r 
        WHERE r.pollOption.poll = p 
        AND r.student.id = :studentId
    )
    ORDER BY p.createdAt DESC
        limit 1
    """)
    Optional<UUID> findLatestUnvotedPollIdByStudentEnrollments(UUID studentId);

}
