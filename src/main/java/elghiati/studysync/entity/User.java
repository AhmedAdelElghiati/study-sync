package elghiati.studysync.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import elghiati.studysync.enums.InstructorType;
import org.hibernate.annotations.UuidGenerator;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import elghiati.studysync.enums.ApprovalStatus;
import elghiati.studysync.enums.Department;
import elghiati.studysync.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @UuidGenerator
    private UUID id;
    @Column(name = "full_name", nullable = false)
    private String fullName;
    @Column(name = "user_name", nullable = false, unique = true)
    private String username;
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
        if(this instanceof Student student && student.isBatchRep()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_BATCH_REP"));
        }
        if(this instanceof Instructor instructor) {
            if(instructor.getInstructorType() == InstructorType.PROFESSOR) {
                authorities.add(new SimpleGrantedAuthority("ROLE_PROFESSOR"));
            } else if(instructor.getInstructorType() == InstructorType.TEACHING_ASSISTANT) {
                authorities.add(new SimpleGrantedAuthority("ROLE_TEACHING_ASSISTANT"));
            }
        }
        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return approvalStatus == ApprovalStatus.APPROVED;
    }
}
