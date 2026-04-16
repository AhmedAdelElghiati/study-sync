package elghiati.studysync.entity;

import elghiati.studysync.enums.Level;
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
@Table(name = "students")
public class Student extends User {
    @Column(name = "level", nullable = false)
    @Enumerated(EnumType.STRING)
    private Level level;
    @Column(name = "gpa", nullable = false)
    private double gpa;
    @Column(name = "seat_number", nullable = false, unique = true)
    private String seatNumber;
}