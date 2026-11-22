package tz.tante.rent.manager.models.dtos.responses.viewings;

import tz.tante.rent.manager.enums.PaymentIntentStatus;
import tz.tante.rent.manager.enums.TanteService;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public record PaymentIntentDetailsDTO(
  Long id,
  Long userAccountId,
  TanteService service,
  String serviceReferenceNumber,
  BigDecimal amount,
  String currency,
  String statusCallbackUrl,
  String paymentLink,
  PaymentIntentStatus status,
  LocalDateTime createdAt,
  LocalDateTime expiresAt,
  LocalDateTime paidAt,
  LocalDateTime refundedAt
)
{
}
