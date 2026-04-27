package tz.tante.rent.manager.models.dtos.requests.Tenants;

public record TenantRequestDto(
        String firstName,
        String lastName,
        String phoneNumber,
        String email
) {
}
