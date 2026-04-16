package elghiati.studysync.entity;

import java.time.Instant;
import java.util.UUID;

import elghiati.studysync.enums.ApprovalStatus;
import elghiati.studysync.enums.Department;
import elghiati.studysync.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
@Table(name = "users")
public class User {
    @Id
    @UuidGenerator
    private UUID id;
    @Column(name = "full_name", nullable = false)
    private String fullName;
    @Column(name = "user_name", nullable = false, unique = true)
    private String userName;
    @Column(name = "university_email", nullable = false, unique = true)
    private String universityEmail;
    @Column(name = "department")
    @Enumerated(EnumType.STRING)
    private Department department;
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    @Column(name = "id_card_path")
    private String idCardPath;
    @Column(name = "approval_status")
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;
    @Column(name = "is_batch_rep", nullable = false)
    private boolean isBatchRep = false;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();
    }
}
