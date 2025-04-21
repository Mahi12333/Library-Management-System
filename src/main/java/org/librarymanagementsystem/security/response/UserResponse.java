package org.librarymanagementsystem.security.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.librarymanagementsystem.model.User;

import java.util.List;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private List<UserInfoResponse> content;
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalPages;
    private boolean lastPage;
}
