package tz.tante.rent.manager.models.dtos.requests.leases;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tz.tante.rent.manager.enums.RentFrequency;
import tz.tante.rent.manager.models.dtos.requests.RentCreateDTO;
import tz.tante.rent.manager.models.dtos.responses.RentDTO;

import java.math.BigDecimal;
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

  RentCreateDTO rent,

  @NotNull(message = "Full lease payment requirement cannot be null")
  boolean fullLeasePaymentRequired
)
{
}
