package elghiati.studysync.repository;

import java.util.List;
import java.util.UUID;

import elghiati.studysync.entity.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import elghiati.studysync.entity.Course;
import elghiati.studysync.enums.Department;
import elghiati.studysync.enums.Level;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
    @Query("""
    SELECT c FROM Course c
    WHERE c.level = :level
    AND (c.departments IS EMPTY
    OR (:department IS NOT NULL AND :department MEMBER OF c.departments))
    """)
    List<Course> findCoursesForStudent(
        @Param("level") Level level,
        @Param("department") Department department
    );

    @Query("""
    SELECT c FROM Course c
    WHERE c.professor = :instructor
    OR :instructor MEMBER OF c.teachingAssistants
    """)
    List<Course> findByInstructor(@Param("instructor") Instructor instructor);

    boolean existsByCode(String code);

}