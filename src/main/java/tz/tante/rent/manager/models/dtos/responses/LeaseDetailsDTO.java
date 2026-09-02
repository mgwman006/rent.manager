package tz.tante.rent.manager.models.dtos.responses;

import tz.tante.rent.manager.enums.RentPeriod;

import java.math.BigDecimal;

public record LeaseDetailsDTO(
  String referenceNumber,
  long id,
  String startDate,
  String endDate,
  BigDecimal rentAmount,
  String currency,
  RentPeriod rentPeriod,
  String status,
  Long tenantId)
{
}
