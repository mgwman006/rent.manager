package tz.tante.rent.manager.models.dtos.requests.payments;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tz.tante.rent.manager.enums.PaymentMethod;
import java.math.BigDecimal;

public record PaymentTransactionCreateDTO(
  @NotNull(message = "Payment block ID cannot be null")
  Long paymentBlockId,
  @NotNull(message = "Amount cannot be null")
  BigDecimal amount,
  @NotBlank(message = "Currency cannot be null")
  String currency,
  @NotNull(message = "Payer user ID cannot be null")
  Long payerUserId,
  String note,
  String reference,
  PaymentMethod method
) {
}
