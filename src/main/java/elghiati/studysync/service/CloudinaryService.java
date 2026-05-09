package elghiati.studysync.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import elghiati.studysync.exception.FileUploadException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String upload(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "auto",
                            "folder", "studysync/materials"
                    )
            );
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new FileUploadException("Failed to upload file: " + e.getMessage());
        }
    }

    public void delete(String fileUrl) {
        try {
            String publicId = extractPublicId(fileUrl);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw new FileUploadException("Failed to delete file: " + e.getMessage());
        }
    }

    private String extractPublicId(String fileUrl) {
        String path = URI.create(fileUrl).getPath();
        int uploadIndex = path.indexOf("/upload/");

        if (uploadIndex < 0) {
            throw new IllegalArgumentException("Invalid Cloudinary URL");
        }

        String afterUpload = path.substring(uploadIndex + "/upload/".length());
        String withoutVersion = afterUpload.replaceFirst("^v\\d+/", "");

        int extensionIndex = withoutVersion.lastIndexOf('.');
        if (extensionIndex > 0) {
            return withoutVersion.substring(0, extensionIndex);
        }

        return withoutVersion;
    }
}