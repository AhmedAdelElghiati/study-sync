package elghiati.studysync.service;

import java.util.UUID;

import elghiati.studysync.exception.DuplicateResourceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import elghiati.studysync.dto.InstructorCreateRequest;
import elghiati.studysync.dto.InstructorResponse;
import elghiati.studysync.dto.InstructorUpdateRequest;
import elghiati.studysync.entity.Instructor;
import elghiati.studysync.enums.ApprovalStatus;
import elghiati.studysync.enums.Role;
import elghiati.studysync.exception.ResourceNotFoundException;
import elghiati.studysync.repository.InstructorRepository;

@Service
public class InstructorService {
    private final InstructorRepository instructorRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public InstructorService(InstructorRepository instructorRepository,
            PasswordEncoder passwordEncoder,
            UserService userService) {
        this.instructorRepository = instructorRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    private InstructorResponse mapToInstructorResponse(Instructor instructor) {
        return new InstructorResponse(
                instructor.getId(),
                instructor.getFullName(),
                instructor.getUserName(),
                instructor.getUniversityEmail(),
                instructor.getDepartment(),
                instructor.getInstructorType(),
                instructor.getIdCardPath(),
                instructor.getApprovalStatus(),
                instructor.getCreatedAt()
        );
    }

    private Instructor findOne(UUID id) {
        return instructorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor with id: " + id + " not found"));
    }

    public InstructorResponse createInstructor(InstructorCreateRequest request) {
        if (userService.findByUserName(request.userName()).isPresent()) {
            throw new DuplicateResourceException("Instructor with username: " + request.userName() + " already exists");
        }

        Instructor instructor = new Instructor();
        instructor.setFullName(request.fullName());
        instructor.setUserName(request.userName());
        instructor.setUniversityEmail(request.universityEmail());
        instructor.setDepartment(request.department());
        instructor.setRole(Role.INSTRUCTOR);
        instructor.setPasswordHash(passwordEncoder.encode(request.password()));
        instructor.setIdCardPath(request.idCardPath());
        instructor.setApprovalStatus(ApprovalStatus.PENDING);
        instructor.setInstructorType(request.instructorType());

        return mapToInstructorResponse(instructorRepository.save(instructor));
    }

    public InstructorResponse getInstructorById(UUID id) {
        return mapToInstructorResponse(findOne(id));
    }

    public InstructorResponse updateInstructor(UUID id, InstructorUpdateRequest request) {
        Instructor instructor = findOne(id);
        if (userService.findByUserName(request.userName()).isPresent() && !instructor.getUserName().equals(request.userName())) {
            throw new DuplicateResourceException("Instructor with username: " + request.userName() + " already exists");
        }
            instructor.setUserName(request.userName());
            instructor.setDepartment(request.department());
            instructor.setIdCardPath(request.idCardPath());
            instructor.setInstructorType(request.instructorType());
        return mapToInstructorResponse(instructorRepository.save(instructor));
    }

    public void deleteInstructor(UUID id) {
        Instructor instructor = findOne(id);
        instructorRepository.delete(instructor);
    }
}