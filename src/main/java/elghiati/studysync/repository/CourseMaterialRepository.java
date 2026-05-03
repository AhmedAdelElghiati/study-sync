package elghiati.studysync.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import elghiati.studysync.entity.CourseMaterial;

public interface CourseMaterialRepository extends JpaRepository<CourseMaterial, UUID> {

    public List<CourseMaterial> findByCourseId(UUID courseId);
}
