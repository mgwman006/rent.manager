package tz.tante.rent.manager.models.dtos.requests.leases;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import tz.tante.rent.manager.models.dtos.requests.RentCreateDTO;

import java.time.LocalDate;

@Schema(description = "Data Transfer Object for creating a new lease.")
public record LeaseCreateDTO(
  @NotNull(message = "Rental profile ID cannot be null")
  long rentalProfileId,

  long unitId,

  Long tenantId,

  String tenantFirstName,

  String tenantLastName,

  String tenantPhoneNumber,

  String landlordFirstName,

  String landlordLastName,

  String landlordPhoneNumber,

  @NotNull(message = "Start date cannot be null")
  LocalDate startDate,

  @NotNull(message = "End date cannot be null")
  LocalDate endDate,

  @NotNull(message = "Rent details cannot be null")
  @Valid
  RentCreateDTO rent,

  @NotNull(message = "Full lease payment requirement cannot be null")
  boolean fullLeasePaymentRequired
)
{
}
