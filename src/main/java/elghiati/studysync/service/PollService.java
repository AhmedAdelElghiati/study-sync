package elghiati.studysync.service;

import elghiati.studysync.dto.PollCreateRequest;
import elghiati.studysync.dto.PollOptionResponse;
import elghiati.studysync.dto.PollResponse;
import elghiati.studysync.dto.PollVoteRequest;
import elghiati.studysync.entity.*;
import elghiati.studysync.exception.BusinessRuleException;
import elghiati.studysync.exception.ResourceNotFoundException;
import elghiati.studysync.repository.PollRepository;
import elghiati.studysync.repository.PollVoteRepository;
import elghiati.studysync.util.CourseAccessValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PollService {
    private final PollRepository pollRepository;
    private final PollVoteRepository pollVoteRepository;
    private final CourseAccessValidator courseAccessValidator;
    private final CourseService courseService;
    public PollService(
            PollRepository pollRepository,
            PollVoteRepository pollVoteRepository,
            CourseAccessValidator courseAccessValidator,
            CourseService courseService
    ) {
        this.pollRepository = pollRepository;
        this.pollVoteRepository = pollVoteRepository;
        this.courseAccessValidator = courseAccessValidator;
        this.courseService = courseService;
    }

    private PollResponse mapToPollResponse(Poll poll , UUID userId) {
        List<PollOptionResponse> pollOptionResponses = mapToPollOptionResponse(poll , userId);
        return new PollResponse(
                poll.getId(),
                poll.getQuestion(),
                poll.getExpiresAt(),
                poll.isMultiAnswer(),
                pollOptionResponses.stream().anyMatch(PollOptionResponse::isSelected),
                pollOptionResponses.stream().mapToLong(PollOptionResponse::votesCount).sum(),
                pollOptionResponses,
                poll.getCreatedBy().getUsername(),
                poll.getCourse().getName(),
                resolveCreatorRole(poll.getCreatedBy())

        );
    }
    private PollResponse mapToPollResponseWithPrefetchedData(Poll poll , Map<UUID , Long> allVoteCounts , Set<UUID> VotedOptions) {
        List<PollOptionResponse> pollOptionResponses = poll.getOptions().stream().map(option -> {
            long voteCount = allVoteCounts.getOrDefault(option.getId(), 0L);
            boolean isVoted = VotedOptions.contains(option.getId());
            return new PollOptionResponse(
                    option.getId(),
                    option.getOptionText(),
                    voteCount,
                    isVoted
            );
        }).collect(Collectors.toList());

        return new PollResponse(
                poll.getId(),
                poll.getQuestion(),
                poll.getExpiresAt(),
                poll.isMultiAnswer(),
                pollOptionResponses.stream().anyMatch(PollOptionResponse::isSelected),
                pollOptionResponses.stream().mapToLong(PollOptionResponse::votesCount).sum(),
                pollOptionResponses,
                poll.getCreatedBy().getUsername(),
                poll.getCourse().getName(),
                resolveCreatorRole(poll.getCreatedBy())
        );
    }

    private List<PollOptionResponse> mapToPollOptionResponse(Poll poll , UUID userId){
        Map<UUID , Long> pollOptionsVoteCount = getPollOptionsVoteCount(poll.getId());
        List<PollOption> options = poll.getOptions();
        Set<UUID> isVoted = isPollVoted(userId , poll.getId());

        List<PollOptionResponse> pollOptionResponses = options.stream().map(option -> {
            long voteCount = pollOptionsVoteCount.getOrDefault(option.getId(), 0L);
            return new PollOptionResponse(
                    option.getId(),
                    option.getOptionText(),
                    voteCount,
                    isVoted.contains(option.getId())
            );
        }).collect(Collectors.toList());
        return pollOptionResponses;
    }

    private Set<UUID> isPollVoted(UUID studentId,UUID pollId){
        return pollVoteRepository.findVotedOptionIdsByStudentAndPollId(studentId , pollId);
    }

    private Map<UUID , Long> getPollOptionsVoteCount(UUID pollId){
        Map<UUID , Long> pollOptionsVoteCount = pollVoteRepository.countVotesByPollId(pollId).stream()
                .collect(Collectors.toMap(PollVoteRepository.PollOptionVoteCount::getPollOptionId , PollVoteRepository.PollOptionVoteCount::getVoteCount));
        return pollOptionsVoteCount;
    }

    private String resolveCreatorRole(User user) {
        if (user instanceof Instructor instructor) {
            return instructor.getInstructorType().name();
        }
        return user.getRole().name();
    }

    public List<PollResponse> getPollsByStudentEnrollments(Student student) {
        List<Poll> polls = pollRepository.findPollsByStudentEnrollments(student.getId());
        if (polls.isEmpty()) {
            log.info("No polls found for student with id {}", student.getId());
            return List.of();
        }

        List<UUID> pollIds = polls.stream().map(Poll::getId).collect(Collectors.toList());

        Map<UUID , Long> allVoteCounts = pollVoteRepository.countVotesByPollIds(pollIds).stream()
                .collect(Collectors.toMap(
                        PollVoteRepository.PollOptionVoteCount::getPollOptionId ,
                        PollVoteRepository.PollOptionVoteCount::getVoteCount
                ));
        Set<UUID> VotedOptions = pollVoteRepository.findVotedOptionIdsByStudentAndPollIds(student.getId() , pollIds);

        return polls.stream()
                .map(poll -> mapToPollResponseWithPrefetchedData(
                        poll,
                        allVoteCounts,
                        VotedOptions)).collect(Collectors.toList());
    }

    public List<PollResponse> getPollsByCourseId(UUID courseId, User user) {
        courseAccessValidator.validateCourseAccess(user, courseId);
        List<Poll> polls = pollRepository.findByCourseId(courseId);
        if (polls.isEmpty()) {
            log.info("No polls found for course with id {}", courseId);
            return List.of();
        }

        List<UUID> pollIds = polls.stream().map(Poll::getId).collect(Collectors.toList());

        Map<UUID , Long> allVoteCounts = pollVoteRepository.countVotesByPollIds(pollIds).stream()
                .collect(Collectors.toMap(
                        PollVoteRepository.PollOptionVoteCount::getPollOptionId ,
                        PollVoteRepository.PollOptionVoteCount::getVoteCount
                ));

        Set<UUID> votedOptions = Set.of();
        if (user instanceof Student student) {
            votedOptions = pollVoteRepository.findVotedOptionIdsByStudentAndPollIds(student.getId(), pollIds);
        }

        Set<UUID> finalVotedOptions = votedOptions;
        return polls.stream()
                .map(poll -> mapToPollResponseWithPrefetchedData(
                        poll,
                        allVoteCounts,
                        finalVotedOptions)).collect(Collectors.toList());
    }

    public Optional<PollResponse> getLatestUnvotedPollByStudentEnrollments(Student student) {
        UUID pollId = pollRepository.findLatestUnvotedPollIdByStudentEnrollments(student.getId()).orElse(null);
        if(pollId == null){
            return Optional.empty();
        }
        Poll poll = pollRepository.findById(pollId).orElseThrow(
                () -> new ResourceNotFoundException("Poll with id " + pollId + " not found")
        );
        return Optional.of(mapToPollResponse(poll , student.getId()));
    }

    public PollResponse createPoll(PollCreateRequest request , User user , UUID courseId) {
        courseAccessValidator.validateCourseAccess(user , courseId);
        Course course = courseService.findById(courseId);
        if(user instanceof Student student && !student.isBatchRep()){
            throw new AccessDeniedException("Only batch representatives can create polls.");
        }

        Poll poll = new Poll();
        poll.setQuestion(request.question());
        poll.setExpiresAt(request.expiresAt());
        poll.setMultiAnswer(request.isMultiAnswer());
        poll.setCourse(course);
        poll.setCreatedBy(user);

        List<PollOption> options = request.options().stream().map(optionText -> {
            PollOption option = new PollOption();
            option.setOptionText(optionText);
            option.setPoll(poll);
            return option;
        }).collect(Collectors.toList());
        poll.setOptions(options);

        return mapToPollResponse(pollRepository.save(poll), user.getId());
    }

    public void deletePoll(UUID courseId , UUID pollId , User user) {
        courseAccessValidator.validateCourseAccess(user , courseId);

        if(user instanceof Student student && !student.isBatchRep()){
            throw new AccessDeniedException("Only batch representatives can delete polls.");
        }

        Poll poll = pollRepository.findById(pollId).orElseThrow(
                () -> new ResourceNotFoundException("Poll with id " + pollId + " not found")
        );

        if (!poll.getCourse().getId().equals(courseId)) {
            throw new ResourceNotFoundException("Poll with id " + pollId + " not found in course " + courseId);
        }
        if(!poll.getCreatedBy().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only delete polls you created");
        }
        pollRepository.delete(poll);
    }

    public PollResponse votePoll(UUID pollId, PollVoteRequest request , User user) {
        Poll poll = pollRepository.findById(pollId).orElseThrow(
                () -> new ResourceNotFoundException("Poll with id " + pollId + " not found")
        );
        courseAccessValidator.validateCourseAccess(user, poll.getCourse().getId());
        if(poll.getExpiresAt().isBefore(Instant.now()))      {
            throw new BusinessRuleException("Poll with id " + pollId + " has expired");
        }
        if(!poll.isMultiAnswer() && !isPollVoted(user.getId(), pollId).isEmpty()) {
            throw new BusinessRuleException("You have already voted for this poll");
        }
        if(poll.isMultiAnswer() && isPollVoted(user.getId(), pollId).contains(request.optionId())) {
            throw new BusinessRuleException("You have already voted for this option");
        }

        PollVote pollVote = new PollVote();
        pollVote.setPollOption(poll.getOptions().stream().filter(option -> option.getId().equals(request.optionId())).findFirst().orElseThrow(
                () -> new ResourceNotFoundException("Poll option with id " + request.optionId() + " not found in poll " + pollId)
        ));
        if(user instanceof Student student){
             pollVote.setStudent(student);
        } else {
            throw new AccessDeniedException("Only students can vote for polls");
        }
        pollVoteRepository.save(pollVote);
        return mapToPollResponse(poll, user.getId());
    }

}