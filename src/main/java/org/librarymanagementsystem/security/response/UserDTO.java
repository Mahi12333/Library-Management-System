package org.librarymanagementsystem.security.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDTO {
    private Long id;
    private String userName;
    private String email;
}
