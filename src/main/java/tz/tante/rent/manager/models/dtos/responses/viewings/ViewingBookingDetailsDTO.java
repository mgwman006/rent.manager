package tz.tante.rent.manager.models.dtos.responses.viewings;

import tz.tante.rent.manager.enums.ViewingStatus;

import java.math.BigDecimal;

public record ViewingBookingDetailsDTO(
  Long id,
  Long slotId,
  Long requesterUserId,
  ViewingStatus status,
  BigDecimal serviceFee,
  boolean feePaid,
  String notes,
  ViewingOutcomeDetailsDTO outcome
) {
}
