package org.librarymanagementsystem.services;

import jakarta.validation.Valid;
import org.librarymanagementsystem.security.request.SignupDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {
    String uploadFile(MultipartFile file) throws IOException;
}
