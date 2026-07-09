package com.dennis.auth.dtos;

import java.time.Instant;

import com.dennis.auth.validation.ICreateUser;
import com.dennis.auth.validation.IUpdateUser;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

public record UserDto(

        @Null(groups = ICreateUser.class, message = "ID must be null when creating a user") @NotNull(groups = IUpdateUser.class, message = "ID is required when updating a user") Long id,
        @NotNull(groups = {
                ICreateUser.class,
                IUpdateUser.class }, message = "Email is required when creating a user") String email,
        @NotNull(groups = { ICreateUser.class,
                IUpdateUser.class }, message = "Role is required when creating a user") String role,
        @NotNull(groups = { ICreateUser.class,
                IUpdateUser.class }, message = "Password is required when creating a user") String password,
        Instant createdAt,
        Instant updatedAt,
        Instant lastLogin) {

}
