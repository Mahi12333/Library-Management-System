package org.librarymanagementsystem.payload.request;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.librarymanagementsystem.emun.ContactusStatus;

@Getter
public class ContactUsDTO {
    private String email;
    private String fullname;
    private String message;
    private String subject;
    private Long user_id;

}
