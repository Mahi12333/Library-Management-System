package org.librarymanagementsystem.security.request;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.librarymanagementsystem.emun.UserStatus;
import org.librarymanagementsystem.model.UserRoleMapping;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;



@Data
public class UpdateUserRequest {
    private Long userId;
    private String userName;
    private String email;
    private Boolean accountNonLocked;
    private Boolean accountNonExpired;
    private Boolean credentialsNonExpired;
    private Boolean enabled;
    private Boolean isTwoFactorEnabled;
    private LocalDate credentialsExpiryDate;
    private LocalDate accountExpiryDate;
    private String phone_number;
    private String country_code;
    private String address;
    private Set<String> roles;
    private String idProof;
    private String status;

}
