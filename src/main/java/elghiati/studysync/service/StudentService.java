package elghiati.studysync.service;

import java.util.UUID;

import elghiati.studysync.exception.DuplicateResourceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import elghiati.studysync.dto.StudentCreateRequest;
import elghiati.studysync.dto.StudentResponse;
import elghiati.studysync.dto.StudentUpdateRequest;
import elghiati.studysync.entity.Student;
import elghiati.studysync.enums.ApprovalStatus;
import elghiati.studysync.enums.Role;
import elghiati.studysync.exception.ResourceNotFoundException;
import elghiati.studysync.repository.StudentRepository;

@Service
public class StudentService {
    final private StudentRepository studentRepository;
    final private PasswordEncoder passwordEncoder;
    final private UserService userService;
    public StudentService(StudentRepository studentRepository, PasswordEncoder passwordEncoder, UserService userService) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    private StudentResponse mapToStudentResponse(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getFullName(),
                student.getUserName(),
                student.getUniversityEmail(),
                student.getDepartment(),
                student.getLevel(),
                student.getSeatNumber(),
                student.getIdCardPath(),
                student.getApprovalStatus(),
                student.getCreatedAt()
        );
    }

    private Student findOne(UUID id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student with id: " + id + " not found"));
    }

    public StudentResponse createStudent(StudentCreateRequest request) {
        if (userService.findByUserName(request.userName()).isPresent()) {
            throw new DuplicateResourceException("Student with username: " + request.userName() + " already exists");
        }
        Student student = new Student();
        student.setFullName(request.fullName());
        student.setUserName(request.userName());
        student.setUniversityEmail(request.universityEmail());
        student.setDepartment(request.department());
        student.setRole(Role.STUDENT);
        student.setPasswordHash(passwordEncoder.encode(request.password()));
        student.setIdCardPath(request.idCardPath());
        student.setApprovalStatus(ApprovalStatus.PENDING);
        student.setLevel(request.level());
        student.setSeatNumber(request.seatNumber());
        return mapToStudentResponse(studentRepository.save(student));
    }

    public StudentResponse getStudentById(UUID id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student with id: " + id + " not found"));
        return mapToStudentResponse(student);
    }

    public StudentResponse updateStudent(UUID id, StudentUpdateRequest request) {
        Student student = findOne(id);
        if (userService.findByUserName(request.userName()).isPresent() && !student.getUserName().equals(request.userName())) {
            throw new DuplicateResourceException("Student with username: " + request.userName() + " already exists");
        }
        student.setUserName(request.userName());
        student.setDepartment(request.department());
        student.setLevel(request.level());
        student.setGpa(request.gpa());
        student.setSeatNumber(request.seatNumber());
        return mapToStudentResponse(studentRepository.save(student));
    }


    public void deleteStudent(UUID id) {
        Student student = findOne(id);
        studentRepository.delete(student);
    }

}