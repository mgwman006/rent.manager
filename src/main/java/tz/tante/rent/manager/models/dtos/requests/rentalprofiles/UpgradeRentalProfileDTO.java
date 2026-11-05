package tz.tante.rent.manager.models.dtos.requests.rentalprofiles;

import jakarta.validation.constraints.NotNull;
import tz.tante.rent.manager.enums.RentalProfileType;


public record UpgradeRentalProfileDTO(
  @NotNull(message = "Rental profile ID is required")
  Long organizationId,

  @NotNull(message = "Rental profile type is required")
  RentalProfileType rentalProfileType)
{
}
