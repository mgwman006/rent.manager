package com.tante.landlordtenant.models.Responses.Users;

import com.tante.landlordtenant.models.Enums.UserStatus;

import java.util.Optional;

public record UpdateUserResponseDto(
        UserStatus status,
        String message,
        Optional<UserResponseDto> userDetails) {
}
