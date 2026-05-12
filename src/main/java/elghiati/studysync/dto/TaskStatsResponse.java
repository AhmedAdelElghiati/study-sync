package elghiati.studysync.dto;

public record TaskStatsResponse(
        long unsubmittedTasks,
        long submittedTasks,
        long totalTask
) {
}
