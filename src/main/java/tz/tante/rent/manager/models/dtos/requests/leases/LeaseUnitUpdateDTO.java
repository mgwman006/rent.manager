package tz.tante.rent.manager.models.dtos.requests.leases;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record LeaseUnitUpdateDTO(
  @NotNull(message = "Unit ID is required")
  @Positive(message = "Unit ID must be positive")
  Long unitId
) {
}
