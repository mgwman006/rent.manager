package tz.tante.rent.manager.models.dtos.requests.Tenants;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record TenantContactUpdateDTO(
  @NotBlank(message = "Phone number is required")
  String phoneNumber,

  String email
)
{
}
