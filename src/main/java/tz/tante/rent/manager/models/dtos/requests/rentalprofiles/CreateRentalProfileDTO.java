package tz.tante.rent.manager.models.dtos.requests.rentalprofiles;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tz.tante.rent.manager.enums.RentalProfileType;

@Schema(description = "Data Transfer Object for creating a new rental profile.")
public record CreateRentalProfileDTO(

  @NotNull(message = "Phone number is required")
  String phoneNumber,

  String email,

  Long userId,

  Long organizationId,

  @NotBlank(message = "Rental profile name is required")
  String name,

  @NotNull(message = "Rental profile type is required")
  RentalProfileType type)
{
}
