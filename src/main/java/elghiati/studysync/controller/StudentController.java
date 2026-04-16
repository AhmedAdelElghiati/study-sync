package elghiati.studysync.controller;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import elghiati.studysync.dto.StudentCreateRequest;
import elghiati.studysync.dto.StudentResponse;
import elghiati.studysync.dto.StudentUpdateRequest;
import elghiati.studysync.service.StudentService;
import elghiati.studysync.shared.APIResponse;
import jakarta.validation.Valid;



@RestController
@RequestMapping("/api/students")
public class StudentController {
    final private StudentService studentService;
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }
    @PostMapping("/register")
    public ResponseEntity<APIResponse<StudentResponse>> registerStudent(@RequestBody @Valid StudentCreateRequest request) {
        StudentResponse studentResponse = studentService.createStudent(request);
        return ResponseEntity.status(CREATED)
        .body(APIResponse.success(studentResponse, "Student registered successfully"));
    }
    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<StudentResponse>> getStudentById(@PathVariable UUID id) {
        StudentResponse studentResponse = studentService.getStudentById(id);
        return ResponseEntity.ok(APIResponse.success(studentResponse, "Student found successfully"));
    }
    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<StudentResponse>> updateStudent(@PathVariable UUID id, @RequestBody @Valid StudentUpdateRequest request) {
        StudentResponse studentResponse = studentService.updateStudent(id, request);
        return ResponseEntity.ok(APIResponse.success(studentResponse, "Student updated successfully"));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteStudent(@PathVariable UUID id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
    
}