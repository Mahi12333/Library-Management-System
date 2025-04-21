package org.librarymanagementsystem.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.librarymanagementsystem.annotation.Auditable;
import org.librarymanagementsystem.emun.UserRole;
import org.librarymanagementsystem.exception.APIException;
import org.librarymanagementsystem.mapstruct.UserMapper;
import org.librarymanagementsystem.model.RefreshToken;
import org.librarymanagementsystem.model.Role;
import org.librarymanagementsystem.model.User;
import org.librarymanagementsystem.model.UserRoleMapping;
import org.librarymanagementsystem.repository.RefreshTokenRepository;
import org.librarymanagementsystem.repository.RoleRepository;
import org.librarymanagementsystem.repository.UserRepository;
import org.librarymanagementsystem.repository.UserRoleMappingRepository;
import org.librarymanagementsystem.security.jwt.JwtUtils;
import org.librarymanagementsystem.security.request.ForgotPasswordDTO;
import org.librarymanagementsystem.security.request.LoginDTO;
import org.librarymanagementsystem.security.request.ResetPasswordDTO;
import org.librarymanagementsystem.security.request.SignupDTO;
import org.librarymanagementsystem.security.response.LoginResponse;
import org.librarymanagementsystem.security.response.UserInfoResponse;
import org.librarymanagementsystem.security.service.UserDetailsImpl;
import org.librarymanagementsystem.services.RefreshTokenService;
import org.librarymanagementsystem.services.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Slf4j
@Tag(name = "AuthController", description = "Auth Management")
@RestController
@RequestMapping("/v1/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final UserRoleMappingRepository userRoleMappingRepository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;

    @Value("${spring.app.jwtRefreshExpirationMs}")
    private int jwtRefreshExpirationMs;

    @Operation(summary = "Create a user-signing ", description = "This API is used to user-signin")
    @PostMapping("/user-login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginDTO loginDTO) {
        log.info("authentication1----");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
        );
        log.info("authentication-- {}",authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String accessToken = jwtUtils.generateAccessToken(userDetails.getId());
        String refreshToken = jwtUtils.generateRefreshToken(userDetails.getId());
        refreshTokenService.deleteByUserId(userDetails.getId());
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setUserId(userDetails.getId());
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setExpiryDate(Instant.now().plus(Duration.ofMillis(jwtRefreshExpirationMs))); // Set expiry to 7 days
        refreshTokenRepository.save(refreshTokenEntity);

        LoginResponse response = new LoginResponse(accessToken, refreshToken, userDetails);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Create a user-refresh ", description = "This API is used to user-resfresh")
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestHeader("Authorization") String refreshTokenHeader) {
        log.info("refreshTokenHeader--{}",refreshTokenHeader);
        if (refreshTokenHeader == null || !refreshTokenHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Refresh Token");
        }

        String refreshToken = refreshTokenHeader.substring(7);
        log.info("storedToken-- {}",refreshToken);
        // ✅ Check if token exists in DB
        RefreshToken storedToken = refreshTokenService.findByToken(refreshToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid or Expired Refresh Token"));
//        log.info("storedToken-- {}",storedToken);
        // ✅ Validate JWT Signature & Expiry
        if (!jwtUtils.validateJwtToken(refreshToken, true)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Expired or Invalid Refresh Token");
        }

        // ✅ Check Expiry
        if (storedToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenService.deleteByUserId(storedToken.getUserId()); // Remove expired token
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Refresh Token Expired. Please log in again.");
        }

        // ✅ Generate new tokens
        Long userId = jwtUtils.getUserIdFromJwtToken(refreshToken, true);
        String newAccessToken = jwtUtils.generateAccessToken(userId);
        String newRefreshToken = jwtUtils.generateRefreshToken(userId);

        // ✅ Update refresh token in DB
        storedToken.setToken(newRefreshToken);
        storedToken.setExpiryDate(Instant.now().plus(Duration.ofMillis(jwtRefreshExpirationMs))); // Example: 7 days expiry
        refreshTokenRepository.save(storedToken);

        // ✅ Return new tokens
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", newAccessToken);
        tokens.put("refresh_token", newRefreshToken);

        return ResponseEntity.ok(tokens);
    }

    @Operation(summary = "Create a Admin-signing ", description = "This API is used to Admin-signing")
    @PostMapping("/admin-login")
    public ResponseEntity<?> authenticateAdminUser(@Valid @RequestBody LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String accessToken = jwtUtils.generateAccessToken(userDetails.getId());
        String refreshToken = jwtUtils.generateRefreshToken(userDetails.getId());

        refreshTokenService.deleteByUserId(userDetails.getId());
        // ✅ Save Refresh Token in Database
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setUserId(userDetails.getId());
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setExpiryDate(Instant.now().plus(Duration.ofMillis(jwtRefreshExpirationMs))); // Set expiry to 7 days
        refreshTokenRepository.save(refreshTokenEntity);

        LoginResponse response = new LoginResponse(accessToken, refreshToken, userDetails.getUsername());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Create a user-signup ", description = "This API is used to user-signup")
    @PostMapping("/public/signup")
    @Transactional
    public ResponseEntity<UserInfoResponse> registerUser(@Valid @RequestBody SignupDTO signupDTO) {
        if (userRepository.existsByUserName(signupDTO.getUserName())) {
            throw  new APIException("Username is already taken!");
        }
        if (userRepository.existsByEmail(signupDTO.getEmail())) {
            throw  new APIException("Email is already taken!");
        }
        if(!signupDTO.getPassword().equals(signupDTO.getConfirmPassword())){
            throw  new APIException("Password and  ConfirmPassword not correct");
        }

       /* // Create new user's account
        User user = new User(signupDTO.getUserName(),
                signupDTO.getEmail(),
                encoder.encode(signupDTO.getPassword()),
                signupDTO.getTc(),
                signupDTO.getAgreeMarketingMaterial()
                );*/

        User user = userMapper.signupDTOToUserMP(signupDTO);
        // Encrypt Password before saving
        user.setPassword(encoder.encode(user.getPassword()));

        final User saveuser = userRepository.save(user);

        Set<String> strRoles = signupDTO.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByRoleName(UserRole.ROLE_STUDENT)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                UserRole userRole = switch (role.toLowerCase()) {
                    case "admin" -> UserRole.ROLE_ADMIN;
                    case "seller" -> UserRole.ROLE_SELLER;
                    case "student" -> UserRole.ROLE_STUDENT;
                    default -> UserRole.ROLE_STUDENT;
                };

                roles.add(roleRepository.findByRoleName(userRole)
                        .orElseThrow(() -> new APIException("Error: Role not found.")));
            });
        }
        // Save role mappings
        roles.forEach(role -> userRoleMappingRepository.save(new UserRoleMapping(saveuser, role)));

        String accessToken = jwtUtils.generateAccessToken(saveuser.getId());
        String refreshToken = jwtUtils.generateRefreshToken(saveuser.getId());

        refreshTokenService.deleteByUserId(saveuser.getId());
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setUserId(saveuser.getId());
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setExpiryDate(Instant.now().plus(Duration.ofMinutes(jwtRefreshExpirationMs))); // Set expiry to 7 days
        refreshTokenRepository.save(refreshTokenEntity);

        return ResponseEntity.ok(new UserInfoResponse(
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
                user.getIsVerified(),
                accessToken,
                refreshToken
                )
        );

    }

    @Operation(summary = "Request Password Reset", description = "Generates a password reset token and sends email.")
    @PostMapping("/public/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordDTO request) {
        userService.generatePasswordResetToken(request.getEmail());
        return ResponseEntity.ok("Password reset email sent!");
    }

    @Operation(summary = "Reset Password", description = "Resets the password using the token.")
    @PostMapping("/public/reset-password")
    public ResponseEntity<?> resetPasswords(@Valid @RequestBody ResetPasswordDTO request) {
        userService.resetPassword(request.getToken(), request.getNewPassword(), request.getComPassword());
        return ResponseEntity.ok("Password has been reset successfully!");
    }



}
