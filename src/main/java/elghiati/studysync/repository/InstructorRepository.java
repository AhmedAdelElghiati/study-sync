package elghiati.studysync.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import elghiati.studysync.entity.Instructor;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, UUID> {
}