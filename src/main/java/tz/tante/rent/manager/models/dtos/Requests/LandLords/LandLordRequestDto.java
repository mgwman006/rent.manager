package tz.tante.rent.manager.models.dtos.Requests.LandLords;

public record LandLordRequestDto(
        String firstName,
        String lastName,
        String phoneNumber,
        String email,
        String passWord
) {
}
