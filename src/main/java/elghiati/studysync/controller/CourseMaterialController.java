package elghiati.studysync.controller;

import elghiati.studysync.dto.CourseMaterialCreateRequest;
import elghiati.studysync.dto.CourseMaterialResponse;
import elghiati.studysync.dto.CourseMaterialUpdateRequest;
import elghiati.studysync.entity.Instructor;
import elghiati.studysync.entity.User;
import elghiati.studysync.service.CourseMaterialService;
import elghiati.studysync.shared.APIResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/courses/{courseId}/materials")
public class CourseMaterialController {
    private final CourseMaterialService courseMaterialService;
    public CourseMaterialController(CourseMaterialService courseMaterialService) {
        this.courseMaterialService = courseMaterialService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<APIResponse<CourseMaterialResponse>> uploadMaterial(
            @ModelAttribute @Valid CourseMaterialCreateRequest request,
            @PathVariable UUID courseId,
            @AuthenticationPrincipal User currentUser
    ) {
        CourseMaterialResponse materialResponse = courseMaterialService.uploadCourseMaterial(request, currentUser, courseId);
        return ResponseEntity
                .status(CREATED)
                .body(APIResponse.success(materialResponse , "Material uploaded successfully"));
    }
    @PutMapping("/{materialId}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<APIResponse<CourseMaterialResponse>> updateMaterial(
            @RequestBody @Valid CourseMaterialUpdateRequest request,
            @PathVariable UUID courseId,
            @PathVariable("materialId") UUID materialId,
            @AuthenticationPrincipal User currentUser
    ) {
        Instructor instructor = (Instructor) currentUser;
        CourseMaterialResponse materialResponse = courseMaterialService.updateCourseMaterial(courseId, materialId, request, instructor);
        return ResponseEntity.ok(APIResponse.success(materialResponse , "Material updated successfully"));
    }
    @DeleteMapping("/{materialId}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<Void> deleteMaterial(
            @PathVariable UUID courseId,
            @PathVariable("materialId") UUID materialId,
            @AuthenticationPrincipal User currentUser
    ) {
        Instructor instructor = (Instructor) currentUser;
        courseMaterialService.deleteMaterial(courseId, materialId, instructor);
        return ResponseEntity.noContent().build();
    }
    @GetMapping
    public ResponseEntity<APIResponse<List<CourseMaterialResponse>>> getMaterials(
            @PathVariable UUID courseId,
            @AuthenticationPrincipal User currentUser
    ) {
        List<CourseMaterialResponse> response = courseMaterialService.getCourseMaterials(courseId , currentUser);
        return ResponseEntity.ok(APIResponse.success(response, "Materials retrieved successfully"));
    }
}
