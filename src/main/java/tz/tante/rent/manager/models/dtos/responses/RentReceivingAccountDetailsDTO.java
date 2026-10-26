package tz.tante.rent.manager.models.dtos.responses;

import tz.tante.rent.manager.enums.MobileMoneyProvider;
import tz.tante.rent.manager.enums.PaymentMethod;

public record RentReceivingAccountDetailsDTO(
  long id,
  String accountNumber,
  String bankName,
  String mobileMoneyNumber,
  MobileMoneyProvider mobileMoneyProvider,
  PaymentMethod paymentMethod,
  boolean isDefault)
{
}
