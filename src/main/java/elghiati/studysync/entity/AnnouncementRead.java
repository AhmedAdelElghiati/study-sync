package elghiati.studysync.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "announcment_read"
, uniqueConstraints = @UniqueConstraint(columnNames = {"announcment_id" , "user_id"}))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnnouncementRead {
    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "announcment_id", nullable = false)
    private Announcement announcement;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "read_at", nullable = false, updatable = false)
    private Instant readAt;
    @PrePersist
    public void onCreate() {
        this.readAt = Instant.now();
    }
}
