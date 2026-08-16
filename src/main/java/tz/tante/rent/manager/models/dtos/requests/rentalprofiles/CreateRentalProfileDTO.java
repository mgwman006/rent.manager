package tz.tante.rent.manager.models.dtos.requests.rentalprofiles;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tz.tante.rent.manager.enums.RentalProfileType;
import tz.tante.rent.manager.models.dtos.requests.CreateRentReceivingAccountDTO;

@Schema(description = "Data Transfer Object for creating a new rental profile.")
public record CreateRentalProfileDTO(
  @NotNull(message = "Phone number is required")
  String phoneNumber,
  @NotNull(message = "Admin user ID is required")
  Long adminUserId,
  @NotBlank(message = "Rental profile name is required")
  String name,
  String businessEmail,
  @NotNull(message = "Rental profile type is required")
  RentalProfileType type,
  CreateRentReceivingAccountDTO rentReceivingAccount)
{
}
