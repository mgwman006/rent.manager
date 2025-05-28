package com.tante.landlordtenant.models.Requests.LandLords;

import jakarta.persistence.Column;

public record LandLordRequestDto(
        String firstName,
        String lastName,
        String phoneNumber,
        String email,
        String passWord
) {
}
