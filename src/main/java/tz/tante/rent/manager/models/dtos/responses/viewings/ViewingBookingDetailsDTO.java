package tz.tante.rent.manager.models.dtos.responses.viewings;

import tz.tante.rent.manager.enums.ViewingBookingStatus;
import tz.tante.rent.manager.enums.ViewingPaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ViewingBookingDetailsDTO(
  Long id,
  String reference,
  Long rentalProfileId,
  Long propertyId,
  LocalDateTime startTime,
  LocalDateTime endTime,
  Long requesterUserId,
  ViewingBookingStatus status,
  BigDecimal serviceFee,
  ViewingPaymentStatus paymentStatus,
  LocalDateTime paymentUpdatedAt,
  LocalDateTime holdExpiresAt,
  String notes,
  ViewingOutcomeDetailsDTO outcome
) {
}
