package tz.tante.rent.manager.models.dtos.responses;

import tz.tante.rent.manager.enums.LeaseInitiator;

import java.util.List;

public record LeaseDetailsDTO(
  String referenceNumber,
  long id,
  LeaseInitiator initiatedBy,
  String startDate,
  String endDate,
  RentDTO rent,
  boolean fullLeasePaymentRequired,
  String status,
  TenantDetailsDTO tenant,
  List<LeaseInvitationDetailsDTO> invitations)
{
}