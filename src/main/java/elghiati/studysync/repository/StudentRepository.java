package elghiati.studysync.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import elghiati.studysync.entity.Student;

@Repository
public interface  StudentRepository extends JpaRepository<Student, UUID> {

}