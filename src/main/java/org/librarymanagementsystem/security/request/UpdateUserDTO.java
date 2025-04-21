package org.librarymanagementsystem.security.request;


import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class UpdateUserDTO {
    private Long id;
    private String userName;
    private String address;
    private String idProof;
    private String profile;
}
