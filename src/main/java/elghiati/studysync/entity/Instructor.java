package elghiati.studysync.entity;

import elghiati.studysync.enums.InstructorType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "instructors")
public class Instructor extends User {
    @Column(name = "instructor_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private InstructorType instructorType;
}