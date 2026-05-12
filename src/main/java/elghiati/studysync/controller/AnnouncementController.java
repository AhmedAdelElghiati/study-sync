package elghiati.studysync.controller;

import com.sun.security.auth.UserPrincipal;
import elghiati.studysync.dto.AnnouncementCreateRequest;
import elghiati.studysync.dto.AnnouncementResponse;
import elghiati.studysync.dto.AnnouncementStatsResponse;
import elghiati.studysync.dto.AnnouncementUpdateRequest;
import elghiati.studysync.entity.Course;
import elghiati.studysync.entity.Student;
import elghiati.studysync.entity.User;
import elghiati.studysync.service.AnnouncementService;
import elghiati.studysync.shared.APIResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api")
public class AnnouncementController {
    private final AnnouncementService announcementService;
    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }
    @Operation(summary = "Get unread announcements for the authenticated student")
    @GetMapping("/announcements/unread")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<APIResponse<List<AnnouncementResponse>>> getUnreadAnnouncementsForStudent(
            @AuthenticationPrincipal User currentUser
    ) {
        Student student = (Student) currentUser;
        List<AnnouncementResponse> announcements = announcementService.getUnreadAnnouncementsForStudent(student);
        return ResponseEntity.ok(APIResponse.success(announcements , "Announcements retrieved successfully"));
    }

    @Operation(summary = "Get read announcements for the authenticated student")
    @GetMapping("/announcements/read")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<APIResponse<List<AnnouncementResponse>>> getreadAnnouncementsForStudent(
            @AuthenticationPrincipal User currentUser
    ) {
        Student student = (Student) currentUser;
        List<AnnouncementResponse> announcements = announcementService.getReadAnnouncementsForStudent(student);
        return ResponseEntity.ok(APIResponse.success(announcements , "Announcements retrieved successfully"));
    }

    @Operation(summary = "Get all announcements which the authenticated user has created")
    @GetMapping("/announcements/me")
    public ResponseEntity<APIResponse<List<AnnouncementResponse>>> getMyAnnouncements(
            @AuthenticationPrincipal User currentUser
    ) {
        List<AnnouncementResponse> announcements = announcementService.getMyAnnouncements(currentUser);
        return ResponseEntity.ok(APIResponse.success(announcements , "Announcements retrieved successfully"));
    }

    @Operation(summary = "Get announcement stats for the authenticated student")
    @GetMapping("/announcements/stats")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<APIResponse<AnnouncementStatsResponse>> getAnnouncementStats(
            @AuthenticationPrincipal User currentUser
    ) {
        Student student = (Student) currentUser;
        AnnouncementStatsResponse stats = announcementService.getAnnouncementStats(student);
        return ResponseEntity.ok(APIResponse.success(stats , "Announcement stats retrieved successfully"));
    }

    @Operation(summary = "Mark an announcement as read")
    @PutMapping("/announcements/{announcementId}/mark-as-read")
    public ResponseEntity<APIResponse<AnnouncementResponse>> markAnnouncementAsRead(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID announcementId
    ) {
        AnnouncementResponse announcement = announcementService.markAnnouncementAsRead(currentUser, announcementId);
        return ResponseEntity.ok(APIResponse.success(announcement, "Announcement marked as read successfully"));
    }
    @Operation(summary = "Create an announcement")
    @PostMapping("/courses/{courseId}/announcements")
    public ResponseEntity<APIResponse<AnnouncementResponse>> createAnnouncement(
            @RequestBody @Valid AnnouncementCreateRequest request,
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID courseId
            ) {
        AnnouncementResponse announcement = announcementService.createAnnouncement(request, currentUser, courseId);
        return ResponseEntity
                .status(CREATED)
                .body(APIResponse.success(announcement, "Announcement created successfully"));
    }
    @Operation(summary = "Update an announcement")
    @PutMapping("/courses/{courseId}/announcements/{announcementId}")
    public ResponseEntity<APIResponse<AnnouncementResponse>> updateAnnouncement(
            @RequestBody @Valid AnnouncementUpdateRequest request,
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID courseId,
            @PathVariable UUID announcementId
    ) {
        AnnouncementResponse announcement = announcementService.updateAnnouncement(request, currentUser, courseId ,announcementId);
        return ResponseEntity.ok(APIResponse.success(announcement, "Announcement updated successfully"));

    }

    @Operation(summary = "Delete an announcement")
    @DeleteMapping("/courses/{courseId}/announcements/{announcementId}")
    public ResponseEntity<APIResponse<Void>> deleteAnnouncement(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID courseId,
            @PathVariable UUID announcementId
    ) {
        announcementService.deleteAnnouncement(currentUser, courseId ,announcementId);
        return ResponseEntity.noContent().build();
    }



}
