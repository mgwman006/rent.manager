package tz.tante.rent.manager.models.dtos.responses.viewings;

import tz.tante.rent.manager.enums.ViewingSlotStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ViewingSlotDetailsDTO(
  Long id,
  Long rentalProfileId,
  Long propertyId,
  LocalDateTime startTime,
  LocalDateTime endTime,
  BigDecimal serviceFee,
  ViewingSlotStatus status,
  String notes
) {
}
