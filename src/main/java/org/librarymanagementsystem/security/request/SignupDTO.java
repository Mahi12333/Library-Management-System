package org.librarymanagementsystem.security.request;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;


@Builder
@Getter
public class SignupDTO {
    private Long id;
    private String userName;
    private String email;
    private String password;
    private String confirmPassword;
    private String tc;
    private String agreeMarketingMaterial;
    private Set<String> roles;

}
