package org.librarymanagementsystem.security.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class ForgotPasswordDTO {
    private String email;
}
