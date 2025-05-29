package com.tante.landlordtenant.models.Requests.Tenants;

public record TenantRequestDto(
        String firstName,
        String lastName,
        String phoneNumber,
        String email
) {
}
