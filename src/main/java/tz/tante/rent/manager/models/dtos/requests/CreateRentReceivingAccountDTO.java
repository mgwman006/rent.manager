package tz.tante.rent.manager.models.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tz.tante.rent.manager.enums.MobileMoneyProvider;
import tz.tante.rent.manager.enums.PaymentMethod;

@Schema(description = "Data Transfer Object for creating a new rent receiving account.")
public record CreateRentReceivingAccountDTO(
  @NotNull(message = "Rental profile ID is required.")
  PaymentMethod paymentMethod,
  String accountNumber,
  String bankName,
  MobileMoneyProvider mobileMoneyProvider,
  String mobileMoneyNumber,
  boolean isDefault)
{
}
