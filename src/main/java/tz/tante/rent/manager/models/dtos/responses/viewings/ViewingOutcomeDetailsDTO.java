package tz.tante.rent.manager.models.dtos.responses.viewings;

import tz.tante.rent.manager.enums.ViewingOutcomeType;

import java.time.LocalDateTime;

public record ViewingOutcomeDetailsDTO(
  Long id,
  Long bookingId,
  ViewingOutcomeType type,
  String notes,
  LocalDateTime recordedAt
) {
}
