package com.tante.landlordtenant.models.Responses.Users;

import java.util.Optional;

public record UserResponseDto(
        Long id,
        String email,
        String passWord) {
}
