package elghiati.studysync.repository;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import elghiati.studysync.entity.Instructor;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, UUID> {
    @Query("SELECT i FROM Instructor i WHERE i.id IN :uuids")
    Set<Instructor> findInstructorsByIds(@Param("uuids") Set<UUID> uuids);
}