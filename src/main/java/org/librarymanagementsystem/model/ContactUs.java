package org.librarymanagementsystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.librarymanagementsystem.emun.ContactusStatus;

import java.sql.Timestamp;


@Entity
@Table(name = "contact_us")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ContactUs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "fullname", nullable = false)
    private String fullname;

    @Lob
    @Column(nullable = false)
    private String subject;

    @Lob
    @Column(nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "contactus_status", nullable = true)
    private ContactusStatus contactusStatus;

    @Column(name = "sent_date", nullable = true)
    private Timestamp sentDate;
}
