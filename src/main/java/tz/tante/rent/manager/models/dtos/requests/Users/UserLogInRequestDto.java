package tz.tante.rent.manager.models.dtos.requests.Users;

public record UserLogInRequestDto(
        String email,
        String passWord) {
}
