package tz.tante.rent.manager.models.dtos.responses.viewings;

import tz.tante.rent.manager.enums.ViewingBookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ViewingBookingDetailsDTO(
  Long id,
  Long rentalProfileId,
  Long propertyId,
  LocalDateTime startTime,
  LocalDateTime endTime,
  Long requesterUserId,
  ViewingBookingStatus status,
  BigDecimal serviceFee,
  boolean feePaid,
  String notes,
  ViewingOutcomeDetailsDTO outcome
) {
}
