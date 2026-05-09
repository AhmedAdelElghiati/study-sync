package elghiati.studysync.entity;

import elghiati.studysync.enums.SubmissionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "submissions" ,
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id" , "task_id"}))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Submission {
    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne( fetch=FetchType.LAZY ,optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne( fetch=FetchType.LAZY ,optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubmissionStatus status;

    @Column(name = "grade", precision = 10, scale = 2)
    private BigDecimal grade;

    @Column(name = "file_url" , nullable = false , columnDefinition = "TEXT")
    private String fileUrl;

    @Column(name = "comment" , columnDefinition = "TEXT")
    private String comment;

    @Column(name = "submitted_at", nullable = false, updatable = false)
    private Instant submittedAt;

    @PrePersist
    public void onCreate() {
        this.submittedAt = Instant.now();
        this.status = SubmissionStatus.SUBMITTED;
    }
}
