package tz.tante.rent.manager.models.dtos.requests.leases;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdateLeaseDatesDTO(
  @NotNull(message = "Start date cannot be null") LocalDate startDate,
  @NotNull(message = "End date cannot be null") LocalDate endDate
)
{
}