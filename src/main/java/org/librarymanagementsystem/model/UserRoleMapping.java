package org.librarymanagementsystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.librarymanagementsystem.annotation.Auditable;

import java.sql.Timestamp;

@Auditable(name = "User Role Audit")
@Entity
@Table(name = "user_role_mappings")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @CreationTimestamp
    private Timestamp assignedAt;

    public UserRoleMapping(User user, Role role) {
        this.user = user;
        this.role = role;
    }

}
