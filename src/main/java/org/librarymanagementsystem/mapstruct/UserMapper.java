package org.librarymanagementsystem.mapstruct;

import org.librarymanagementsystem.model.User;
import org.librarymanagementsystem.security.request.LoginDTO;
import org.librarymanagementsystem.security.request.SignupDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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

    List<User> getAllUsersMP(List<User> users);

}
