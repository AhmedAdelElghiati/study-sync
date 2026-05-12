package elghiati.studysync.dto;

public record AnnouncementStatsResponse(
        long unreadAnnouncements,
        long readAnnouncements,
        long totalAnnouncements
) {
}
