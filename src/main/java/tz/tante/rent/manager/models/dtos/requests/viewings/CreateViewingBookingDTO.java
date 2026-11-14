package tz.tante.rent.manager.models.dtos.requests.viewings;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateViewingBookingDTO(
  @NotNull(message = "Rental profile ID is required") Long rentalProfileId,
  @NotNull(message = "Unit ID is required") Long unitId,
  @NotNull(message = "Viewing start time is required") LocalDateTime startTime,
  LocalDateTime endTime,
  @NotNull(message = "Requester user account ID is required") Long requesterUserAccountId,
  @NotNull(message = "Viewing fee is required") @DecimalMin(value = "0.0", message = "Viewing fee cannot be negative") BigDecimal serviceFee,
  String notes
) {
}
