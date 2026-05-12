package elghiati.studysync.repository;

import elghiati.studysync.entity.Announcement;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, UUID> {
    @Query("""
    SELECT a
    FROM Announcement a
    JOIN FETCH a.sender
    JOIN FETCH a.course
    WHERE EXISTS (
        SELECT e
        FROM Enrollment e
        WHERE e.course.id = a.course.id
        AND e.student.id = :studentId
    )
    AND EXISTS (
        SELECT ar
        FROM AnnouncementRead ar
        WHERE ar.announcement.id = a.id
        AND ar.user.id = :studentId
    )
    ORDER BY a.createdAt DESC
   
    """)
    List<Announcement> findReadAnnouncementsForStudent(@Param("studentId") UUID studentId);

    @Query("""
    SELECT a
    FROM Announcement a
    JOIN FETCH a.sender
    JOIN FETCH a.course
    WHERE EXISTS (
        SELECT e
        FROM Enrollment e
        WHERE e.course.id = a.course.id
        AND e.student.id = :studentId
    )
    AND NOT EXISTS (
        SELECT ar
        FROM AnnouncementRead ar
        WHERE ar.announcement.id = a.id
        AND ar.user.id = :studentId
    )
    ORDER BY a.createdAt DESC
    """)
    List<Announcement> findUnreadAnnouncementsForStudent(@Param("studentId") UUID studentId);
    @Query("""
    SELECT a
    FROM Announcement a
    JOIN FETCH a.sender
    JOIN FETCH a.course
    WHERE a.sender.id = :senderId
    ORDER BY a.createdAt DESC
    """)
    List<Announcement> findAnnouncementsBySenderId(@Param("senderId") UUID senderId);

    @EntityGraph(attributePaths = {"sender" , "course"})
    Optional<Announcement> getAnnouncementsById(UUID announcementId);

    @Query("""
    SELECT COUNT(a)
    FROM Announcement a
    WHERE EXISTS (
        SELECT e
        FROM Enrollment e
        WHERE e.course.id = a.course.id
        AND e.student.id = :studentId
    )
    AND NOT EXISTS (
        SELECT ar
        FROM AnnouncementRead ar
        WHERE ar.announcement.id = a.id
        AND ar.user.id = :studentId
    )
    """)
    long countUnreadAnnouncementsForStudent(@Param("studentId") UUID studentId);

    @Query("""
    SELECT COUNT(a)
    FROM Announcement a
    WHERE EXISTS (
        SELECT e
        FROM Enrollment e
        WHERE e.course.id = a.course.id
        AND e.student.id = :studentId
    )
    AND EXISTS (
        SELECT ar
        FROM AnnouncementRead ar
        WHERE ar.announcement.id = a.id
        AND ar.user.id = :studentId
    )
    """)
    long countReadAnnouncementsForStudent(@Param("studentId") UUID studentId);

}