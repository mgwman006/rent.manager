package com.tante.landlordtenant.models.Responses.Tenants;

public record TenantResponseDto(
        Long id,
        String firstName,
        String lastName,
        String phoneNumber,
        String email
) {
}
