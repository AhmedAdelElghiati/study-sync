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
@Table(name = "tasks")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne( fetch= FetchType.LAZY ,optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne( fetch= FetchType.LAZY ,optional = false)
    @JoinColumn(name = "assigned_by_id", nullable = false)
    private User assignedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "deadline", nullable = false)
    private Instant deadline;

    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();
    }
}
