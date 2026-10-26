package tz.tante.rent.manager.models.dtos.requests.leaseinvitation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LeaseInvitationCreateDTO(
  @NotNull(message = "Lease ID is required")
  Long leaseId,

  @NotBlank(message = "First name is required")
  String firstName,

  @NotBlank(message = "Last name is required")
  String lastName,

  @NotBlank(message = "Phone number is required")
  String phoneNumber,

  String email
)
{
}
