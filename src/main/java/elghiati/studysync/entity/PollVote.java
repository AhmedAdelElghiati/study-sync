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
@Table(
        name = "poll_votes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"poll_option_id" , "student_id"}
))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PollVote {
    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY , optional = false)
    @JoinColumn(name = "poll_option_id", nullable = false)
    private PollOption pollOption;

    @ManyToOne(fetch = FetchType.LAZY , optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;


    @Column(name = "responded_at", nullable = false)
    private Instant respondedAt;

    @PrePersist
    public void onCreate() {
        respondedAt = Instant.now();
    }
}
