package tz.tante.rent.manager.models.dtos.requests.viewings;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateViewingSlotDTO(
  @NotNull(message = "Property ID is required") Long propertyId,
  @NotNull(message = "Viewing start time is required") LocalDateTime startTime,
  @NotNull(message = "Viewing end time is required") LocalDateTime endTime,
  @NotNull(message = "Viewing fee is required") @DecimalMin(value = "0.0", inclusive = true, message = "Viewing fee must not be negative") BigDecimal serviceFee,
  String notes
) {
}
