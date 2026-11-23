package tz.tante.rent.manager.models.dtos.requests.viewings;

import tz.tante.rent.manager.enums.TanteService;
import java.math.BigDecimal;

public record CreateGenericPaymentIntentDTO(
  Long userAccountId,
  BigDecimal amount,
  String currency,
  TanteService service,
  String serviceReferenceNumber,
  String callbackUrl
) {
}