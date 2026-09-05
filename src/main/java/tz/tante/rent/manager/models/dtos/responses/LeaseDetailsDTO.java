package tz.tante.rent.manager.models.dtos.responses;

import tz.tante.rent.manager.enums.PaymentPeriod;
import tz.tante.rent.manager.enums.RentPeriod;

import java.math.BigDecimal;
import java.util.List;

public record LeaseDetailsDTO(
  String referenceNumber,
  long id,
  String startDate,
  String endDate,
  BigDecimal rentAmount,
  String currency,
  RentPeriod rentPeriod,
  PaymentPeriod paymentPeriod,
  BigDecimal paymentAmount,
  BigDecimal amountPaid,
  BigDecimal balance,
  String status,
  TenantDetailsDTO tenant,
  List<TenantInvitationDetailsDTO> tenantInvitations)
{
}