package dev.arun.ecomuserservice.mapper;

import dev.arun.ecomuserservice.dto.UserDto;
import dev.arun.ecomuserservice.models.User;

public class UserEntityDTOMapper {
    public static UserDto getUserDTOFromUserEntity(User user){
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setRoles(user.getRoles());
        return userDto;
    }
}