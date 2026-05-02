package elghiati.studysync.repository;

import elghiati.studysync.entity.Course;
import elghiati.studysync.entity.Instructor;
import elghiati.studysync.enums.Department;
import elghiati.studysync.enums.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
    @Query("""
    SELECT c FROM Course c
    WHERE c.level = :level
    AND (c.departments IS EMPTY
    OR (:department IS NOT NULL AND :department MEMBER OF c.departments))
    AND NOT EXISTS (
        SELECT e FROM Enrollment e
        WHERE e.course = c
        AND e.student.id = :studentId
        )
    """)
    List<Course> findCoursesForStudent(
        @Param("level") Level level,
        @Param("department") Department department,
        @Param("studentId") UUID studentId
    );

    @Query("""
    SELECT c FROM Course c
    WHERE c.professor = :instructor
    OR :instructor MEMBER OF c.teachingAssistants
    """)
    List<Course> findByInstructor(@Param("instructor") Instructor instructor);

    boolean existsByCode(String code);

}