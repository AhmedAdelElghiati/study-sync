package elghiati.studysync.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "announcments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Announcement {
    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY , optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY , optional = false)
    @JoinColumn(name = "sender", nullable = false)
    private User sender;

    @OneToMany(mappedBy = "announcement", cascade = CascadeType.REMOVE, orphanRemoval = true)
    Set<AnnouncementRead> reads = new HashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();
    }
}
