package tz.tante.rent.manager.models.dtos.requests.Users;

public record UpdateUserRequestDto(
        String firstName,
        String lastName,
        String email,
        String passWord) {
}
