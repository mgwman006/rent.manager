package tz.tante.rent.manager.models.dtos.requests.leases;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tz.tante.rent.manager.enums.LeaseStatus;
import tz.tante.rent.manager.enums.RentPeriod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Data Transfer Object for creating a new lease.")
public record LeaseCreateDTO(
  @NotNull(message = "Rental profile ID cannot be null")
  long rentalProfileId,
  long unitId,
  Long tenantId,
  String tenantFirstName,
  String tenantLastName,
  String tenantPhoneNumber,
  @NotNull(message = "Start date cannot be null")
  LocalDateTime startDate,
  @NotNull(message = "End date cannot be null")
  LocalDateTime endDate,
  @NotNull(message = "Rent amount cannot be null")
  BigDecimal rentAmount,
  @NotBlank(message = "Currency cannot be null")
  String currency,
  @NotNull(message = "Rent period cannot be null")
  RentPeriod rentPeriod
) {}
