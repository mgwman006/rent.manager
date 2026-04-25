package tz.tante.rent.manager.models.dtos.Responses.LandLords;

public record LandLordResponseDto(
        Long id,
        String firstName,
        String lastName,
        String phoneNumber,
        String email
) {
}
