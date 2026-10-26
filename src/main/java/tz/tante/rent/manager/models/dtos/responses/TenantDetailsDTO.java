package tz.tante.rent.manager.models.dtos.responses;

public record TenantDetailsDTO(
  Long id,
  Long userId,
  String firstName,
  String lastName,
  String email,
  String phoneNumber)
{
}
