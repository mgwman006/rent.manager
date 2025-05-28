package com.tante.landlordtenant.models.Responses.LandLords;

public record LandLordResponseDto(
        Long id,
        String firstName,
        String lastName,
        String phoneNumber,
        String email
) {
}
