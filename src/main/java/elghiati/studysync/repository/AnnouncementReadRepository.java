package elghiati.studysync.repository;

import elghiati.studysync.entity.AnnouncementRead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AnnouncementReadRepository extends JpaRepository<AnnouncementRead, UUID> {
     boolean existsByAnnouncementIdAndUserId(UUID announcementId, UUID userId);

}
