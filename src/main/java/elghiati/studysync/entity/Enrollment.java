package elghiati.studysync.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import elghiati.studysync.enums.EnrollmentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "enrollments",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "course_id"}))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Enrollment {

    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY , optional = false)
    @JoinColumn(name = "student_id" , nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY , optional = false)
    @JoinColumn(name = "course_id" , nullable = false)
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status;

    @Column(name = "enrolled_at", nullable = false, updatable = false)
    private Instant enrolledAt;
    @Column(name = "re_enrolled_at")
    private Instant reEnrolledAt;

    @PrePersist
    public void onCreate() {
        this.enrolledAt = Instant.now();
        this.reEnrolledAt = null;
    }
}

