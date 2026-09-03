package tz.tante.rent.manager.models.dtos.requests.leases;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tz.tante.rent.manager.enums.RentPeriod;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Data Transfer Object for creating a new lease.")
public record LeaseCreateDTO(
  @NotNull(message = "Rental profile ID cannot be null")
  long rentalProfileId,

  long unitId,

  Long tenantId,

  @NotBlank(message = "Tenant first name cannot be null")
  String tenantFirstName,

  @NotBlank(message = "Tenant last name cannot be null")
  String tenantLastName,

  @NotBlank(message = "Tenant phone number cannot be null")
  String tenantPhoneNumber,

  @NotNull(message = "Start date cannot be null")
  LocalDate startDate,

  @NotNull(message = "End date cannot be null")
  LocalDate endDate,

  @NotNull(message = "Rent amount cannot be null")
  BigDecimal rentAmount,

  @NotBlank(message = "Currency cannot be null")
  String currency,

  @NotNull(message = "Rent period cannot be null")
  RentPeriod rentPeriod
) {}
