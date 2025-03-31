package org.librarymanagementsystem.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.librarymanagementsystem.emun.UserRole;
import org.librarymanagementsystem.exception.APIException;
import org.librarymanagementsystem.model.Role;
import org.librarymanagementsystem.model.User;
import org.librarymanagementsystem.repository.RefreshTokenRepository;
import org.librarymanagementsystem.repository.UserRoleMappingRepository;
import org.librarymanagementsystem.security.request.UpdatePasswordDTO;
import org.librarymanagementsystem.security.request.UpdateUserRequest;
import org.librarymanagementsystem.security.response.UserInfoResponse;
import org.librarymanagementsystem.security.response.UserResponse;
import org.librarymanagementsystem.services.UserService;
import org.librarymanagementsystem.utils.AuthUtil;
import org.librarymanagementsystem.utils.constants.ConstantValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;



@Slf4j
@Tag(name = "UserController", description = "User Management")
@RestController
@RequestMapping("/v1/api/user")
@RequiredArgsConstructor
public class UserController {
    // TODO: Implement user management endpoints
     private final UserService userService;
     private final AuthUtil authUtil;
     private final RefreshTokenRepository refreshTokenRepository;
     private final UserRoleMappingRepository userRoleMappingRepository;


    /*@Operation(summary = "Request Password Reset", description = "Generates a password reset token and sends email.")
    @PostMapping("/public/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordDTO request) {
        userService.generatePasswordResetToken(request.getEmail());
        return ResponseEntity.ok( new APIException("Password reset email sent!"));
    }

    @Operation(summary = "Reset Password", description = "Resets the password using the token.")
    @PostMapping("/public/reset-password")
    public ResponseEntity<?> resetPasswords(@Valid @RequestBody ResetPasswordDTO request) {
        userService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(new APIException("Password has been reset successfully!"));
    }*/

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "User Details", description = " Get user details of the logged-in user.")
    @GetMapping("/userinfo")
    public ResponseEntity<?> getUserDetails(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("User Details--- ");
        log.info("User Details--- {}", userDetails);
        User user = userService.findByUsername(userDetails.getUsername());
        Set<Role> roles = userDetails.getAuthorities().stream()
                .map(item -> UserRole.valueOf(item.getAuthority()))
                .map(Role::new)  // Assuming Role has a constructor Role(UserRole role)
                .collect(Collectors.toSet());

        UserInfoResponse response = new UserInfoResponse(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                user.getAccountNonLocked(),
                user.getAccountNonExpired(),
                user.getCredentialsNonExpired(),
                user.getEnabled(),
                user.getCredentialsExpiryDate(),
                user.getAccountExpiryDate(),
                user.getIsTwoFactorEnabled(),
                roles,
                user.getIdProof(),
                user.getStatus(),
                user.getTerm_condition_material(),
                user.getAgree_marketing_material(),
                user.getFcm_token(),
                user.getProfile(),
                user.getPhone_number(),
                user.getCountry_code(),
                user.getAddress(),
                user.getIsVerified()
        );
        return ResponseEntity.ok().body(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "User Edit", description = "Edit User details of a specific user.")
    @PostMapping("/user-edit")
    public ResponseEntity<?> editUser(@Valid @RequestBody Long userId) {
        User user = userService.getUserById(userId);

        // Convert List<String> to Set<Role>
        Set<Role> roles = userRoleMappingRepository.findRolesByUserId(user.getId()).stream()
                .map(roleName -> new Role(UserRole.valueOf(roleName))) // Convert each role name to Role object
                .collect(Collectors.toSet());

        // Build response
        UserInfoResponse response = new UserInfoResponse(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                user.getAccountNonLocked(),
                user.getAccountNonExpired(),
                user.getCredentialsNonExpired(),
                user.getEnabled(),
                user.getCredentialsExpiryDate(),
                user.getAccountExpiryDate(),
                user.getIsTwoFactorEnabled(),
                roles,  // Include roles
                user.getIdProof(),
                user.getStatus(),
                user.getTerm_condition_material(),
                user.getAgree_marketing_material(),
                user.getFcm_token(),
                user.getProfile(),
                user.getPhone_number(),
                user.getCountry_code(),
                user.getAddress(),
                user.getIsVerified()
        );

        return ResponseEntity.ok(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "User Edit", description = "Edit User details of a specific user.")
    @PutMapping("/user-updated")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UpdateUserRequest request) {
        User user = userService.getUserById(request.getUserId());

        // Update user details
        user.setUserName(request.getUserName());
        user.setEmail(request.getEmail());
        user.setAccountNonLocked(request.getAccountNonLocked());
        user.setAccountNonExpired(request.getAccountNonExpired());
        user.setCredentialsNonExpired(request.getCredentialsNonExpired());
        user.setEnabled(request.getEnabled());
        user.setIsTwoFactorEnabled(request.getIsTwoFactorEnabled());
        user.setCredentialsExpiryDate(request.getCredentialsExpiryDate());
        user.setAccountExpiryDate(request.getAccountExpiryDate());
        user.setPhone_number(request.getPhone_number());
        user.setCountry_code(request.getCountry_code());
        user.setAddress(request.getAddress());

        // Update user roles
        Set<Role> roles = request.getRoles().stream()
                .map(roleName -> new Role(UserRole.valueOf(roleName))) // Convert role name to Role object
                .collect(Collectors.toSet());

//        // Remove old roles and assign new ones
//        userRoleMappingRepository.deleteByUserId(user.getId()); // Clear old roles
//        roles.forEach(role -> userRoleMappingRepository.save(new UserRoleMapping(user, role)));
//
//        // Save updated user
//        userService.saveUser(user);

        return new ResponseEntity<>(null, HttpStatus.OK);
    }



    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update Password", description = "Allows logged-in users to change their password.")
    @PostMapping("/update-password")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody UpdatePasswordDTO request) {
        Long userId = authUtil.loggedInUserId();
        userService.updatePassword(userId, request);
        return ResponseEntity.ok(new APIException("Password updated successfully!"));
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Soft delete a user", description = "Marks a user as deleted instead of permanently removing them.")
    @DeleteMapping("/delete-user")
    public ResponseEntity<?> userDelete(@Valid @RequestBody Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("User delete successfully");
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get All user", description = "  This Api used to get all user.")
    @GetMapping("/all-user")
    public ResponseEntity<UserResponse> getAllUser(@RequestParam(name = "keyword", required = false) String keyword,
                                                             @RequestParam(name = "pageNumber", defaultValue = ConstantValue.PAGE_NUMBER, required = false) Integer pageNumber,
                                                             @RequestParam(name = "pageSize", defaultValue = ConstantValue.PAGE_SIZE, required = false) Integer pageSize,
                                                             @RequestParam(name = "sortBy", defaultValue = ConstantValue.SORT_USERS_BY, required = false) String sortBy,
                                                             @RequestParam(name = "sortOrder", defaultValue = ConstantValue.SORT_DIR, required = false) String sortOrder) {
        UserResponse userResponse = userService.getallUser(pageNumber, pageSize, sortBy, sortOrder, keyword);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);

    }


    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a logout ", description = "This API is used to logout")
    @PostMapping("/user-logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String refreshToken) {
        SecurityContextHolder.clearContext();
        String token = refreshToken.substring(7);
        refreshTokenRepository.deleteByToken(token);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }


}
