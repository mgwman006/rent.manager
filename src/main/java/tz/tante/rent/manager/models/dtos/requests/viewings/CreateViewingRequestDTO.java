package tz.tante.rent.manager.models.dtos.requests.viewings;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotNull;

public record CreateViewingRequestDTO(
  @NotNull(message = "Viewing slot ID is required") Long slotId,
  @NotNull(message = "Requester user ID is required") @JsonAlias({"tenantUserId"}) Long requesterUserId,
  String notes
) {
}
