package elghiati.studysync.entity;

import elghiati.studysync.enums.MaterialType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "course_materials")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseMaterial {
    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private MaterialType type;

    @ManyToOne( fetch= FetchType.LAZY ,optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne( fetch= FetchType.LAZY ,optional = false)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;

    @Column(name = "url", nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();
    }

}
