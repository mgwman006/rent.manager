package tz.tante.rent.manager.models.dtos.Responses.Tenants;

public record TenantResponseDto(
        Long id,
        String firstName,
        String lastName,
        String phoneNumber,
        String email
) {
}
