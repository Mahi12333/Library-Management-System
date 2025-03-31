package org.librarymanagementsystem.security.response;


import lombok.Getter;
import lombok.Setter;
import org.librarymanagementsystem.emun.UserStatus;
import org.librarymanagementsystem.model.Role;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Setter
@Getter
public class UserInfoResponse {
    private Long id;
    private String userName;
    private String email;
    private Boolean accountNonLocked;
    private Boolean accountNonExpired;
    private Boolean credentialsNonExpired;
    private Boolean enabled;
    private LocalDate credentialsExpiryDate;
    private LocalDate accountExpiryDate;
    private Boolean isTwoFactorEnabled;
    private Set<Role> roles;
    private String idProof;
    private UserStatus status;
    private String term_condition_material;
    private String agree_marketing_material;
    private String fcm_token;
    private String profile;
    private String phone_number;
    private String country_code;
    private String address;
    private Boolean isVerified;
    private String accessToken;
    private String refreshToken;

    public UserInfoResponse(Long id, String userName, String email, Boolean accountNonLocked, Boolean accountNonExpired, Boolean credentialsNonExpired, Boolean enabled, LocalDate credentialsExpiryDate, LocalDate accountExpiryDate, Boolean isTwoFactorEnabled, Set<Role> roles, String idProof, UserStatus status, String term_condition_material, String agree_marketing_material, String fcm_token, String profile, String phone_number, String country_code, String address, Boolean isVerified, String accessToken, String refreshToken) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.accountNonLocked = accountNonLocked;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;
        this.credentialsExpiryDate = credentialsExpiryDate;
        this.accountExpiryDate = accountExpiryDate;
        this.isTwoFactorEnabled = isTwoFactorEnabled;
        this.roles = roles;
        this.idProof = idProof;
        this.status = status;
        this.term_condition_material = term_condition_material;
        this.agree_marketing_material = agree_marketing_material;
        this.fcm_token = fcm_token;
        this.profile = profile;
        this.phone_number = phone_number;
        this.country_code = country_code;
        this.address = address;
        this.isVerified = isVerified;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public UserInfoResponse(Long id, String userName, String email, Boolean accountNonLocked, Boolean accountNonExpired, Boolean credentialsNonExpired, Boolean enabled, LocalDate credentialsExpiryDate, LocalDate accountExpiryDate, Boolean isTwoFactorEnabled, Set<Role> roles, String idProof, UserStatus status, String term_condition_material, String agree_marketing_material, String fcm_token, String profile, String phone_number, String country_code, String address, Boolean isVerified) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.accountNonLocked = accountNonLocked;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;
        this.credentialsExpiryDate = credentialsExpiryDate;
        this.accountExpiryDate = accountExpiryDate;
        this.isTwoFactorEnabled = isTwoFactorEnabled;
        this.roles = roles;
        this.idProof = idProof;
        this.status = status;
        this.term_condition_material = term_condition_material;
        this.agree_marketing_material = agree_marketing_material;
        this.fcm_token = fcm_token;
        this.profile = profile;
        this.phone_number = phone_number;
        this.country_code = country_code;
        this.address = address;
        this.isVerified = isVerified;
    }
}