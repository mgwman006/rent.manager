package tz.tante.rent.manager.engines.rent.dtos;

import java.math.BigDecimal;

public record MonthlyCollectionSummary(
  BigDecimal totalExpectedAmount,
  BigDecimal totalAmountPaid,
  BigDecimal totalOutstandingAmount,
  double progressPercentage
)
{
}
