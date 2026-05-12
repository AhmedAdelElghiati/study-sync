package elghiati.studysync.service;

import elghiati.studysync.dto.AnnouncementCreateRequest;
import elghiati.studysync.dto.AnnouncementResponse;
import elghiati.studysync.dto.AnnouncementStatsResponse;
import elghiati.studysync.dto.AnnouncementUpdateRequest;
import elghiati.studysync.entity.*;
import elghiati.studysync.exception.ResourceNotFoundException;
import elghiati.studysync.repository.AnnouncementReadRepository;
import elghiati.studysync.repository.AnnouncementRepository;
import elghiati.studysync.util.CourseAccessValidator;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AnnouncementService {
    private final AnnouncementRepository announcementRepository;
    private final AnnouncementReadRepository announcementReadRepository;
    private final CourseService courseService;
    private final CourseAccessValidator courseAccessValidator;
    public AnnouncementService(
            AnnouncementRepository announcementRepository,
            AnnouncementReadRepository announcementReadRepository,
            CourseService courseService,
            CourseAccessValidator courseAccessValidator
    ) {
        this.announcementRepository = announcementRepository;
        this.announcementReadRepository = announcementReadRepository;
        this.courseService = courseService;
        this.courseAccessValidator = courseAccessValidator;
    }
    private AnnouncementResponse mapToAnnouncementResponse(Announcement announcement) {
        return new AnnouncementResponse(
                announcement.getId(),
                announcement.getTitle(),
                announcement.getContent(),
                announcement.getCourse().getName(),
                announcement.getSender().getFullName(),
                resolveSenderRole(announcement.getSender()),
                announcement.getCreatedAt()
        );
    }
    private String resolveSenderRole(User sender) {
        if (sender instanceof Instructor instructor) {
            return instructor.getInstructorType().name();
        }
        return sender.getRole().name();
    }
    @Transactional(readOnly = true)
    public List<AnnouncementResponse> getUnreadAnnouncementsForStudent(Student student) {
        List<Announcement> announcements = announcementRepository.findUnreadAnnouncementsForStudent(student.getId());
        return announcements.stream()
                .map(this::mapToAnnouncementResponse)
                .toList();
    }
    @Transactional(readOnly = true)
    public List<AnnouncementResponse> getReadAnnouncementsForStudent(Student student) {
        List<Announcement> announcements = announcementRepository.findReadAnnouncementsForStudent(student.getId());
        return announcements.stream()
                .map(this::mapToAnnouncementResponse)
                .toList();
    }

    @Transactional
    public AnnouncementResponse markAnnouncementAsRead(User user , UUID announcementId) {
        Announcement announcement = announcementRepository.getAnnouncementsById(announcementId)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement with id " + announcementId + " not found"));
        Course course = announcement.getCourse();
        courseAccessValidator.validateCourseAccess(user , course.getId());
        if(announcementReadRepository.existsByAnnouncementIdAndUserId(announcementId , user.getId())){
             return mapToAnnouncementResponse(announcement);
        }
        AnnouncementRead announcementRead = new AnnouncementRead();
        announcementRead.setAnnouncement(announcement);
        announcementRead.setUser(user);
        announcementReadRepository.save(announcementRead);
        return mapToAnnouncementResponse(announcement);
    }

    @Transactional
    public AnnouncementResponse createAnnouncement(AnnouncementCreateRequest request , User user , UUID courseId) {
        courseAccessValidator.validateCourseAccess(user , courseId);
        if(user instanceof Student student  && !student.isBatchRep()) {
            throw new AccessDeniedException("Only batch representatives can create announcements");
        }
        Course course = courseService.findById(courseId);
        Announcement announcement = new Announcement();
        announcement.setTitle(request.title());
        announcement.setContent(request.content());
        announcement.setSender(user);
        announcement.setCourse(course);
        return mapToAnnouncementResponse(announcementRepository.save(announcement));
    }

    @Transactional
    public AnnouncementResponse updateAnnouncement(
            AnnouncementUpdateRequest request,
            User user,
            UUID courseId,
            UUID announcementId
    ) {
        Announcement announcement = announcementRepository.getAnnouncementsById(announcementId)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement with id " + announcementId + " not found"));
        Course course = announcement.getCourse();
        if(!courseId.equals(announcement.getCourse().getId())) {
            throw new ResourceNotFoundException("Announcement with id " + announcementId + " not found in course with id " + courseId);
        }
        courseAccessValidator.validateCourseAccess(user , course.getId());
        if(!announcement.getSender().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only update your own announcements");
        }
        announcement.setTitle(request.title());
        announcement.setContent(request.content());
        return mapToAnnouncementResponse(announcementRepository.save(announcement));
    }

    @Transactional
    public void deleteAnnouncement(
            User user,
            UUID courseId,
            UUID announcementId
    ) {
        Announcement announcement = announcementRepository.getAnnouncementsById(announcementId)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement with id " + announcementId + " not found"));
        Course course = announcement.getCourse();
        if(!courseId.equals(announcement.getCourse().getId())) {
            throw new ResourceNotFoundException("Announcement with id " + announcementId + " not found in course with id " + courseId);
        }
        courseAccessValidator.validateCourseAccess(user , course.getId());
        if(!announcement.getSender().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only delete your own announcements");
        }
        announcementRepository.delete(announcement);
    }

    @Transactional(readOnly = true)
    public List<AnnouncementResponse> getMyAnnouncements(User user) {
        List<Announcement> announcements = announcementRepository.findAnnouncementsBySenderId(user.getId());
        return announcements.stream()
                .map(this::mapToAnnouncementResponse)
                .toList();
    }
    @Transactional(readOnly = true)
    public AnnouncementStatsResponse getAnnouncementStats(Student student) {
        long readAnnouncements = announcementRepository.countReadAnnouncementsForStudent(student.getId());
        long unreadAnnouncements = announcementRepository.countUnreadAnnouncementsForStudent(student.getId());
        long totalAnnouncements = readAnnouncements + unreadAnnouncements;
        return new AnnouncementStatsResponse(unreadAnnouncements, readAnnouncements, totalAnnouncements);
    }

}
