package org.librarymanagementsystem.services;

import org.librarymanagementsystem.model.User;
import org.librarymanagementsystem.security.request.UpdatePasswordDTO;
import org.librarymanagementsystem.security.request.UpdateUserDTO;
import org.librarymanagementsystem.security.response.UserInfoResponse;
import org.librarymanagementsystem.security.response.UserResponse;
import org.springframework.web.multipart.MultipartFile;


public interface UserService {
    //@Auditable(name = "Generate Password ResetToken Audit")
    void generatePasswordResetToken(String email);

    //@Auditable(name = "Get all User Audit")
    void resetPassword(String token, String newPassword, String comPassword);

    User findByUsername(String username);

    User getUserById(Long userId);

    //@Auditable(name = "Delete User Audit")
    void deleteUser(Long userId);

    //@Auditable(name = "UpdatePassword Audit")
    void updatePassword(Long userId, UpdatePasswordDTO request);

    //@Auditable(name = "Get all User Audit")
    UserResponse getallUser(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, String keyword);

    UserInfoResponse updateUserDetails(UpdateUserDTO request);
}
