package elghiati.studysync.repository;

import elghiati.studysync.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByCourseId(UUID courseId);
}
