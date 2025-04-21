package org.librarymanagementsystem.mapstruct;

import org.librarymanagementsystem.model.User;
import org.librarymanagementsystem.security.request.LoginDTO;
import org.librarymanagementsystem.security.request.SignupDTO;
import org.librarymanagementsystem.security.request.UpdateUserDTO;
import org.librarymanagementsystem.security.response.UserInfoResponse;
import org.librarymanagementsystem.security.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Optional;


@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "tc", target = "term_condition_material")
    @Mapping(source = "agreeMarketingMaterial", target = "agree_marketing_material")
    @Mapping(target = "userRoles", ignore = true) // Handle in service
    User signupDTOToUserMP(SignupDTO signupDTO);

    User UserByIDMP(User user);

    //List<UserInfoResponse> getAllUsersMP(List<User> users);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "userName", source = "userName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "enabled", source = "enabled")
    @Mapping(target = "accountNonLocked", source = "accountNonLocked")
    @Mapping(target = "accountNonExpired", source = "accountNonExpired")
    @Mapping(target = "credentialsNonExpired", source = "credentialsNonExpired")
    UserInfoResponse toUserInfoResponse(User user);

    void updateUserFromDto(UpdateUserDTO dto, @MappingTarget User entity);
    UserInfoResponse userUpdatetoDto(User entity);
}
