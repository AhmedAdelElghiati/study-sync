package elghiati.studysync.controller;

import java.util.List;
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

import elghiati.studysync.dto.InstructorCreateRequest;
import elghiati.studysync.dto.InstructorResponse;
import elghiati.studysync.dto.InstructorUpdateRequest;
import elghiati.studysync.service.InstructorService;
import elghiati.studysync.shared.APIResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/instructors")
public class InstructorController {
    private final InstructorService instructorService;

    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @PostMapping("/register")
    public ResponseEntity<APIResponse<InstructorResponse>> registerInstructor(@RequestBody @Valid InstructorCreateRequest request) {
        InstructorResponse instructorResponse = instructorService.createInstructor(request);
        return ResponseEntity.status(CREATED)
                .body(APIResponse.success(instructorResponse, "Instructor registered successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<InstructorResponse>> getInstructorById(@PathVariable UUID id) {
        InstructorResponse instructorResponse = instructorService.getInstructorById(id);
        return ResponseEntity.ok(APIResponse.success(instructorResponse, "Instructor found successfully"));
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<InstructorResponse>>> getAllInstructors() {
        List<InstructorResponse> instructorResponses = instructorService.getAllInstructors();
        return ResponseEntity.ok(APIResponse.success(instructorResponses, "Instructors retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<InstructorResponse>> updateInstructor(@PathVariable UUID id,
            @RequestBody @Valid InstructorUpdateRequest request) {
        InstructorResponse instructorResponse = instructorService.updateInstructor(id, request);
        return ResponseEntity.ok(APIResponse.success(instructorResponse, "Instructor updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteInstructor(@PathVariable UUID id) {
        instructorService.deleteInstructor(id);
        return ResponseEntity.noContent().build();
    }
}