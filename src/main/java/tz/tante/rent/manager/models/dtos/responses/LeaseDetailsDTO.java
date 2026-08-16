package tz.tante.rent.manager.models.dtos.responses;

import java.math.BigDecimal;

public record LeaseDetailsDTO(
  String referenceNumber,
  long id,
  String startDate,
  String endDate,
  BigDecimal rentAmount,
  String status,
  long tenantId)
{
}
