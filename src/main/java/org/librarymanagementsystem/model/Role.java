package org.librarymanagementsystem.model;

import jakarta.persistence.*;
import lombok.*;
import org.librarymanagementsystem.annotation.Auditable;
import org.librarymanagementsystem.emun.UserRole;

@Auditable(name = "Role Audit")
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ToString.Exclude
    @Enumerated(EnumType.STRING)
    @Column(name = "role_name")
    private UserRole roleName;


    public Role(UserRole roleName) {
        this.roleName = roleName;
    }

}
