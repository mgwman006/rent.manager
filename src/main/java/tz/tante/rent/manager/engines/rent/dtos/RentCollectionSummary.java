package tz.tante.rent.manager.engines.rent.dtos;

import tz.tante.rent.manager.enums.PaymentBlockStatus;
import tz.tante.rent.manager.enums.RentCollectionStatus;

import java.math.BigDecimal;
import java.util.List;

public record RentCollectionSummary(
  BigDecimal totalExpectedAmount,
  BigDecimal totalPaidAmount,
  BigDecimal totalOutstandingAmount,
  RentCollectionStatus status,
  List<PaymentBlockSummary> paymentBlocks
)
{
}
