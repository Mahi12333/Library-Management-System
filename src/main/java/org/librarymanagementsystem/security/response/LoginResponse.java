package org.librarymanagementsystem.security.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String username;
    private Object details;


    public LoginResponse(String accessToken, String refreshToken, String username) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.username = username;
    }
    //! Login Response
    public LoginResponse(String accessToken, String refreshToken, Object details) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.details = details;
    }
}
