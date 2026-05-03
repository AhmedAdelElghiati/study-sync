package elghiati.studysync.service;

import elghiati.studysync.dto.CourseMaterialCreateRequest;
import elghiati.studysync.dto.CourseMaterialResponse;
import elghiati.studysync.dto.CourseMaterialUpdateRequest;
import elghiati.studysync.entity.Course;
import elghiati.studysync.entity.CourseMaterial;
import elghiati.studysync.entity.Instructor;
import elghiati.studysync.entity.User;
import elghiati.studysync.enums.MaterialType;
import elghiati.studysync.exception.BusinessRuleException;
import elghiati.studysync.exception.ResourceNotFoundException;
import elghiati.studysync.repository.CourseMaterialRepository;
import elghiati.studysync.util.CourseAccessValidator;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CourseMaterialService {
    private final CourseMaterialRepository courseMaterialRepository;
    private final CourseService courseService;
    private final CloudinaryService cloudinaryService;
    private final CourseAccessValidator courseAccessValidator;

    public CourseMaterialService(
            CourseMaterialRepository courseMaterialRepository,
            CourseService courseService,
            CloudinaryService cloudinaryService,
            CourseAccessValidator courseAccessValidator
    ) {
        this.courseMaterialRepository = courseMaterialRepository;
        this.courseService = courseService;
        this.cloudinaryService = cloudinaryService;
        this.courseAccessValidator = courseAccessValidator;
    }

    private CourseMaterialResponse mapToMaterialResponse(CourseMaterial courseMaterial) {
        return new CourseMaterialResponse(
                courseMaterial.getId(),
                courseMaterial.getTitle(),
                courseMaterial.getType(),
                courseMaterial.getUploadedBy().getFullName(),
                courseMaterial.getUrl(),
                courseMaterial.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<CourseMaterialResponse> getCourseMaterials(UUID courseId , User currentUser) {
        courseAccessValidator.validateCourseAccess(currentUser , courseId);
        return courseMaterialRepository.findByCourseId(courseId).stream()
                .map(this::mapToMaterialResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CourseMaterialResponse uploadCourseMaterial(
            CourseMaterialCreateRequest request,
            User uploadedBy,
            UUID courseId
    ) {

        Course course = courseService.findById(courseId);
        courseAccessValidator.validateCourseAccess(uploadedBy , courseId);

        String url;
        if(request.type() == MaterialType.LINK) {
            if(request.url() == null || request.url().isBlank()) {
                throw new BusinessRuleException("URL is required for link materials");
            }
            url = request.url();
        } else if(request.type() == MaterialType.FILE || request.type() == MaterialType.GRADES) {
            if(request.file() == null || request.file().isEmpty()) {
                throw new BusinessRuleException("File is required for file materials");
            }
            url = cloudinaryService.upload(request.file());
        } else {
            throw new BusinessRuleException("Unsupported material type");
        }

        CourseMaterial courseMaterial = new CourseMaterial();
        courseMaterial.setTitle(request.title());
        courseMaterial.setType(request.type());
        courseMaterial.setUrl(url);
        courseMaterial.setUploadedBy(uploadedBy);
        courseMaterial.setCourse(course);

        return mapToMaterialResponse(courseMaterialRepository.save(courseMaterial));
    }

    @Transactional
    public CourseMaterialResponse updateCourseMaterial(
            UUID courseId,
            UUID materialId,
            CourseMaterialUpdateRequest request,
            Instructor owner
    ) {
        CourseMaterial courseMaterial = courseMaterialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Material with id: " + materialId + " not found"
                ));
        if (!courseMaterial.getCourse().getId().equals(courseId)) {
            throw new BusinessRuleException("Material does not belong to this course");
        }
        if(!courseMaterial.getUploadedBy().getId().equals(owner.getId())) {
            throw new AccessDeniedException("You can only update materials you uploaded");
        }
        courseMaterial.setTitle(request.title());
        return mapToMaterialResponse(courseMaterialRepository.save(courseMaterial));
    }

    @Transactional
    public void deleteMaterial(UUID courseId, UUID materialId, Instructor owner) {
        CourseMaterial material = courseMaterialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Material with id: " + materialId + " not found"
                ));

        if (!material.getCourse().getId().equals(courseId)) {
            throw new BusinessRuleException("Material does not belong to this course");
        }
        if(!material.getUploadedBy().getId().equals(owner.getId())) {
            throw new AccessDeniedException("You can only delete materials you uploaded");
        }
        courseMaterialRepository.delete(material);
    }

}
