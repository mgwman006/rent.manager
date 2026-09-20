package tz.tante.rent.manager.models.dtos.responses;

import tz.tante.rent.manager.enums.LeaseInitiator;
import tz.tante.rent.manager.enums.RentFrequency;
import java.math.BigDecimal;
import java.util.List;

public record LeaseDetailsDTO(
  String referenceNumber,
  long id,
  LeaseInitiator initiatedBy,
  String startDate,
  String endDate,
  BigDecimal rentAmount,
  String currency,
  RentFrequency rentFrequency,
  boolean fullLeasePaymentRequired,
  String status,
  TenantDetailsDTO tenant,
  List<LeaseInvitationDetailsDTO> invitations)
{
}