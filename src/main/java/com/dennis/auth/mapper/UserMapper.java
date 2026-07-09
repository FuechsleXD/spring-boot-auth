package com.dennis.auth.mapper;

import org.mapstruct.Mapper;

import com.dennis.auth.dtos.UserDto;
import com.dennis.auth.models.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    User toEntity(UserDto userDto);

}
