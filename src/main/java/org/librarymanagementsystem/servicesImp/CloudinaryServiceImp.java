package org.librarymanagementsystem.servicesImp;

import com.cloudinary.Cloudinary;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.librarymanagementsystem.exception.APIException;
import org.librarymanagementsystem.security.request.SignupDTO;
import org.librarymanagementsystem.services.CloudinaryService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class CloudinaryServiceImp implements CloudinaryService {
    @Resource
    private Cloudinary cloudinary;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB (fixed typo in comment)
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "application/pdf",
            "text/xml",
            "image/png",
            "image/jpg",
            "image/jpeg",
            "video/mp4"
    );
    private static final String FOLDER_NAME = "user_uploads";
    private static final Map<String, String> DEFAULT_OPTIONS = Map.of(
            "folder", FOLDER_NAME,
            "resource_type", "auto"
    );

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        // Null/empty check
        if (file == null) {
            throw new APIException("File cannot be null or empty");
        }
        if (file.isEmpty()) {
            throw new APIException("File cannot be null or empty");
        }
        if (file.getOriginalFilename() == null) {
            throw new APIException("Filename is null");
        }

        // Content-Type validation (case-insensitive)
        String contentType = file.getContentType().toLowerCase();
        if (ALLOWED_CONTENT_TYPES.stream().noneMatch(allowed -> allowed.equalsIgnoreCase(contentType))) {
            log.debug("Allowed types: {}", ALLOWED_CONTENT_TYPES);
            throw new APIException("Only PDF, XML, PNG, JPG, JPEG, or MP4 files are allowed");
        }

        // Size validation
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new APIException("File size exceeds 5MB limit");
        }

        log.info("Uploading file: {} ({} bytes, {})",
                file.getOriginalFilename(),
                file.getSize(),
                file.getContentType());

        try {
            Map<String, Object> options = new HashMap<>(DEFAULT_OPTIONS);
            options.put("public_id", generatePublicId(file.getOriginalFilename()));

            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
            String imageUrl = (String) uploadResult.get("secure_url");

            log.info("Upload successful: {}", imageUrl);
            return imageUrl;
        } catch (IOException e) {
            log.error("Upload failed for file: {}", file.getOriginalFilename(), e);
            throw new IOException("File upload failed. Please try again.", e);
        }
    }

    private String generatePublicId(String originalFilename) {
        String sanitizedFilename = originalFilename.replaceAll("[^a-zA-Z0-9.-]", "_");
        return FOLDER_NAME + "/" + UUID.randomUUID() + "_" + sanitizedFilename;
    }
}
