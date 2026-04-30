package elghiati.studysync.entity;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import elghiati.studysync.enums.Department;
import elghiati.studysync.enums.Level;
import elghiati.studysync.enums.Semester;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "courses")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Course {
    @Id
    @UuidGenerator
    private UUID id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "code" , nullable = false, unique = true)
    private String code;
    @Column(name = "description" , columnDefinition = "TEXT")
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private Level level;
    @Column(name = "semester", nullable = false)
    private Semester semester;
    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "course_departments", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "department")
    private Set<Department> departments = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "professor_id", nullable = false)
    private Instructor professor;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "course_teachingAssistants",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "instructor_id")
    )
    private Set<Instructor> teachingAssistants = new HashSet<>();

    @Column(name = "created_at", updatable = false , nullable = false)
    private Instant createdAt;
    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();
    }

}
