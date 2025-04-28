package org.librarymanagementsystem.servicesImp;

import com.cloudinary.Cloudinary;
import jakarta.annotation.Resource;
import org.librarymanagementsystem.security.request.SignupDTO;
import org.librarymanagementsystem.services.CloudinaryService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryServiceImp implements CloudinaryService {
    @Resource
    private Cloudinary cloudinary;

    private static final String FOLDER_NAME = "user_uploads"; // Optional, or get from config

    @Override
    public Map<String, String> uploadFile(MultipartFile profile, MultipartFile idProof) throws IOException {
        Map<String, Object> options = new HashMap<>();
        options.put("folder", FOLDER_NAME);

        // Upload profile photo
        Map profileUploadResult = cloudinary.uploader().upload(profile.getBytes(), options);
        String profileUrl = (String) profileUploadResult.get("secure_url");

        // Upload ID proof
        Map idProofUploadResult = cloudinary.uploader().upload(idProof.getBytes(), options);
        String idProofUrl = (String) idProofUploadResult.get("secure_url");

        // Return both URLs
        Map<String, String> urls = new HashMap<>();
        urls.put("profileUrl", profileUrl);
        urls.put("idProofUrl", idProofUrl);

        return urls;

    }
}
