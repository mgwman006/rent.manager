package tz.tante.rent.manager.models.dtos.requests.viewings;

import jakarta.validation.constraints.NotNull;
import tz.tante.rent.manager.enums.ViewingOutcomeType;

public record RecordViewingOutcomeDTO(
  @NotNull(message = "Outcome is required") ViewingOutcomeType outcome,
  String notes
) {
}
