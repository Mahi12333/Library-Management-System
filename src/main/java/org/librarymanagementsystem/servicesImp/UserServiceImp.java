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
import org.librarymanagementsystem.security.response.UserInfoResponse;
import org.librarymanagementsystem.security.response.UserResponse;
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

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final UserMapper userMapper;


    @Value("${FORGET_PASSWORD_URL}")
    private String FORGET_PASSWORD_URL;

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
        emailService.sendEmail(user.getEmail(), Subject, htmlContent);
    }

    @Override
    public void resetPassword(String token, String newPassword, String comPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("user", "Invalid password reset token", token));

        if (resetToken.getExpiryDate().isBefore(Instant.now())) {
            throw new APIException("Token has expired. Please request a new one.");
        }

        if(newPassword.equals(comPassword)){
            throw new APIException("New password cannot be the same as the confirm password.");
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


    @Override
    public User getUserById(Long userId) {
        User userDB = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user", "user", userId));
        return userMapper.UserByIDMP(userDB);
    }

    @Override
    public void deleteUser(Long userId) {
        User userDB = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user", "user", userId));
        userRepository.delete(userDB);
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

    @Override
    public UserResponse getallUser(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, String keyword) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Specification<User> spec = Specification.where(null);
        //!This is a lambda function that defines the filtering logic.
        if (keyword != null && !keyword.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("userName")), "%" + keyword.toLowerCase() + "%"));
        }
        Page<User> usersPage = userRepository.findAll(spec, pageDetails);
        List<User> users = usersPage.getContent();
        if (users.isEmpty()) {
            throw new APIException("No User Exist Now!");
        }

        List<User> userDTOS = userMapper.getAllUsersMP(users);

        UserResponse userResponse = new UserResponse();
        userResponse.setContent(userDTOS);
        userResponse.setPageNumber(usersPage.getNumber());
        userResponse.setLastPage(usersPage.isLast());
        userResponse.setPageSize(usersPage.getSize());
        userResponse.setTotalElements(usersPage.getTotalElements());
        userResponse.setTotalPages(usersPage.getTotalPages());

        return userResponse;
    }
}
