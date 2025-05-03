package org.librarymanagementsystem.servicesImp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.librarymanagementsystem.exception.APIException;
import org.librarymanagementsystem.exception.ResourceNotFoundException;
import org.librarymanagementsystem.mapstruct.UserMapper;
import org.librarymanagementsystem.model.PasswordResetToken;
import org.librarymanagementsystem.model.User;
import org.librarymanagementsystem.repository.PasswordResetTokenRepository;
import org.librarymanagementsystem.repository.UserRepository;
import org.librarymanagementsystem.security.request.UpdatePasswordDTO;
import org.librarymanagementsystem.security.request.UpdateUserDTO;
import org.librarymanagementsystem.security.response.UserInfoResponse;
import org.librarymanagementsystem.security.response.UserResponse;
import org.librarymanagementsystem.services.CloudinaryService;
import org.librarymanagementsystem.services.UserService;
import org.librarymanagementsystem.utils.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final UserMapper userMapper;
    private final NotificationServiceImp notificationServiceImp;
    private final CloudinaryService cloudinaryService;


    @Value("${FORGET_PASSWORD_URL}")
    private String FORGET_PASSWORD_URL;

    @Transactional
    @Override
    public void generatePasswordResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new APIException("User with email " + email + " not found"));

        // Remove old token (if any)
        passwordResetTokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plus(24, ChronoUnit.HOURS);
        PasswordResetToken resetToken = new PasswordResetToken(user, expiryDate, token);
        passwordResetTokenRepository.save(resetToken);

        // Create Reset URL
        String resetUrl = FORGET_PASSWORD_URL + "/reset-password?token=" + token;
        String Subject = "Reset Your Password";
        String htmlContent = "<p>Click the link below to reset your password:</p>"
                + "<a href=\"" + resetUrl + "\">Reset Password</a>";
        // Send email
        emailService.sendEmail(user.getEmail(), Subject, htmlContent, null);
    }

    @Transactional
    @Override
    public void resetPassword(String token, String newPassword, String comPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("user", "Invalid password reset token", token));

        if (resetToken.getExpiryDate().isBefore(Instant.now())) {
            throw new APIException("Token has expired. Please request a new one.");
        }

        if (!newPassword.equals(comPassword)) {
            throw new APIException("New password and confirm password do not match.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetTokenRepository.delete(resetToken);
    }

    @Override
    public User findByUsername(String username) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return userMapper.UserByIDMP(user);
    }

    @Transactional
    @Override
    public User getUserById(Long userId) {
        User userDB = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user", "user", userId));
        return userMapper.UserByIDMP(userDB);
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        User userDB = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user", "user", userId));
        userRepository.delete(userDB);
        notificationServiceImp.accountDeletionNotification(userDB);
    }

    @Override
    public void updatePassword(Long userId, UpdatePasswordDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new APIException("User not found"));
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new APIException("Old password is incorrect");
        }
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new APIException("New password cannot be the same as the old password");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

    }

    @Transactional
    @Override
    public UserResponse getallUser(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, String keyword) {
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, getSortOrder(sortBy, sortOrder));
        Specification<User> spec = getUserSpecification(keyword);

        Page<User> usersPage = userRepository.findAll(spec, pageDetails);

        if (usersPage.isEmpty()) {
            throw new APIException("No Users Exist!");
        }

        // Convert Users to DTOs
        List<UserInfoResponse> userDTOS = usersPage.getContent()
                .stream()
                .map(userMapper::toUserInfoResponse)
                .collect(Collectors.toList());

        // Build Response
        return buildUserResponse(usersPage, userDTOS);
    }

    /**
     * Returns sorting order based on input.
     */
    private Sort getSortOrder(String sortBy, String sortOrder) {
        return sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
    }

    /**
     * Builds a Specification for filtering users by keyword.
     */
    private Specification<User> getUserSpecification(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Specification.where(null);
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("userName")), "%" + keyword.toLowerCase() + "%");
    }

    /**
     * Builds a UserResponse object.
     */
    private UserResponse buildUserResponse(Page<User> usersPage, List<UserInfoResponse> userDTOS) {
        return new UserResponse(
                userDTOS,
                usersPage.getNumber(),
                usersPage.getSize(),
                usersPage.getTotalElements(),
                usersPage.getTotalPages(),
                usersPage.isLast()
        );
    }

    @Transactional
    @Override
    public UserInfoResponse updateUserDetails(UpdateUserDTO request) {
        User existingUser = userRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("user","User not found"));

        if(isUserChanged(request, existingUser)){
            userMapper.updateUserFromDto(request, existingUser);
            existingUser.setUpdatedAt(LocalDateTime.now());
            User updatedUser = userRepository.save(existingUser);
            return userMapper.userUpdatetoDto(updatedUser);
        }else {
            log.info("No changes detected. Skipping update.");
            throw new APIException("No changes detected. Skipping update.");
        }
    }

    public boolean isUserChanged(UpdateUserDTO dto, User user) {
        if (!dto.getUserName().equals(user.getUserName())) return true;
        if (!dto.getAddress().equals(user.getAddress())) return true;
        if (!dto.getProfile().equals(user.getProfile())) return true;
        if (!dto.getIdProof().equals(user.getIdProof())) return true;

        // If nothing changed
        return false;
    }


}
