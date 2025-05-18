package com.tante.landlordtenant.models.Requests.Users;

public record UpdateUserRequestDto(
        String firstName,
        String lastName,
        String email,
        String passWord) {
}
