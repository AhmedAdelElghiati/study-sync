package elghiati.studysync.controller;

import elghiati.studysync.dto.PollCreateRequest;
import elghiati.studysync.dto.PollResponse;
import elghiati.studysync.dto.PollVoteRequest;
import elghiati.studysync.entity.Student;
import elghiati.studysync.entity.User;
import elghiati.studysync.service.PollService;
import elghiati.studysync.shared.APIResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api")
public class PollController {
    private final PollService pollService;
    public PollController(PollService pollService) {
        this.pollService = pollService;
    }

    @GetMapping("/polls")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<APIResponse<List<PollResponse>>> getPollsForStudent(
            @AuthenticationPrincipal Student student
    ) {
        List<PollResponse> polls = pollService.getPollsByStudentEnrollments(student);
        return ResponseEntity.ok(APIResponse.success(polls , "Polls retrieved successfully"));
    }

    @GetMapping("/polls/latest")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<APIResponse<PollResponse>> getLatestUnvotedPollForStudent(
            @AuthenticationPrincipal Student student
    ) {
        Optional<PollResponse> poll = pollService.getLatestUnvotedPollByStudentEnrollments(student);
        return poll.map(pollResponse -> ResponseEntity.ok(APIResponse.success(pollResponse, "Latest unvoted poll retrieved successfully"))).
                orElseGet(() -> ResponseEntity.ok(APIResponse.success(null, "No unvoted polls found")));
    }

    @PostMapping("/polls/{pollId}/vote")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<APIResponse<PollResponse>> votePoll(
            @AuthenticationPrincipal Student student,
            @RequestBody @Valid PollVoteRequest request,
            @PathVariable UUID pollId
    ) {
        PollResponse pollResponse = pollService.votePoll(pollId, request , student);
        return ResponseEntity.ok(APIResponse.success(pollResponse, "Vote recorded successfully"));
    }

    @GetMapping("/courses/{courseId}/polls")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('BATCH_REPRESENTATIVE')")
    public ResponseEntity<APIResponse<List<PollResponse>>> getPollsByCourseId(
            @AuthenticationPrincipal User user,
            @PathVariable UUID courseId
    ) {
        List<PollResponse> polls = pollService.getPollsByCourseId(courseId, user);
        return ResponseEntity.ok(APIResponse.success(polls, "Polls retrieved successfully"));
    }

    @PostMapping("/courses/{courseId}/polls")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('BATCH_REPRESENTATIVE')")
    public ResponseEntity<APIResponse<PollResponse>> createPoll(
            @AuthenticationPrincipal User user,
            @PathVariable UUID courseId,
            @RequestBody @Valid PollCreateRequest request
    ) {
        PollResponse pollResponse = pollService.createPoll(request, user, courseId);
        return ResponseEntity
                .status(CREATED)
                .body(APIResponse.success(pollResponse, "Poll created successfully"));

    }

    @DeleteMapping("/courses/{courseId}/polls/{pollId}")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('BATCH_REPRESENTATIVE')")
    public ResponseEntity<APIResponse<Void>> deletePoll(
            @AuthenticationPrincipal User user,
            @PathVariable UUID courseId,
            @PathVariable UUID pollId
    ) {
        pollService.deletePoll(courseId, pollId, user);
        return ResponseEntity.noContent().build();
    }


}
