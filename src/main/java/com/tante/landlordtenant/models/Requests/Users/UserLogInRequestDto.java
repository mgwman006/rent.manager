package com.tante.landlordtenant.models.Requests.Users;

public record UserLogInRequestDto(
        String email,
        String passWord) {
}
