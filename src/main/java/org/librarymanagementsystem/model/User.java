package org.librarymanagementsystem.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.librarymanagementsystem.annotation.Auditable;
import org.librarymanagementsystem.emun.UserStatus;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Auditable(name = " User Audit")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username", nullable = false)
    private String userName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(name = "password", nullable = false)
    private String password;

    private Boolean accountNonLocked = true;
    private Boolean accountNonExpired = true;
    private Boolean credentialsNonExpired = true;
    private Boolean enabled = true;

    private LocalDate credentialsExpiryDate;
    private LocalDate accountExpiryDate;

    private String twoFactorSecret;
    private Boolean isTwoFactorEnabled = false;

    @Column(name = "signUpMethod", nullable = true)
    private String signUpMethod;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRoleMapping> userRoles = new HashSet<>();

    @Column(name = "idProof", nullable = true)
    private String idProof;

    @ToString.Exclude
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus status;

    @Column(name = "term_condition_material", nullable = true)
    private String term_condition_material;

    @Column(name = "agree_marketing_material", nullable = true)
    private String agree_marketing_material;

    @Column(name = "fcm_token", nullable = true)
    private String fcm_token;

    @Column(name = "profile", nullable = true)
    private String profile;

    @Column(name = "phone_number", nullable = true)
    private String phone_number;

    @Column(name = "country_code", nullable = true)
    private String country_code;

    @Lob
    @Column(name = "address", nullable = true)
    private String address;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false; // Default to false


    public User(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    public User(String userName, String email, String password, String agree_marketing_material, String term_condition_material) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.agree_marketing_material = agree_marketing_material;
        this.term_condition_material = term_condition_material;
    }
}
