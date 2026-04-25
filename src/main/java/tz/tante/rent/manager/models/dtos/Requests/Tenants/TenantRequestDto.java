package tz.tante.rent.manager.models.dtos.Requests.Tenants;

public record TenantRequestDto(
        String firstName,
        String lastName,
        String phoneNumber,
        String email
) {
}
