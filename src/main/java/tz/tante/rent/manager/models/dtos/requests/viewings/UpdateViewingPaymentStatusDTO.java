package tz.tante.rent.manager.models.dtos.requests.viewings;

import jakarta.validation.constraints.NotNull;
import tz.tante.rent.manager.enums.ViewingPaymentStatus;

public record UpdateViewingPaymentStatusDTO(
  @NotNull(message = "Payment status is required") ViewingPaymentStatus paymentStatus,
  String reason
) {
}
