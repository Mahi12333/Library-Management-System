package org.librarymanagementsystem.security.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private Object details;

    public LoginResponse(String accessToken, String refreshToken, Object details) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.details = details;
    }
}
