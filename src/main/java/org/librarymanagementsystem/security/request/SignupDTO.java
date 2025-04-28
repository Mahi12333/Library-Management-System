package org.librarymanagementsystem.security.request;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

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
    private String profile;
    private String idProof;

}
