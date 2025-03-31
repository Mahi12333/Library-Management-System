package org.librarymanagementsystem.security.request;

import lombok.Getter;

@Getter
public class ResetPasswordDTO {
    private String token;
    private String newPassword;
    private String comPassword;
}
