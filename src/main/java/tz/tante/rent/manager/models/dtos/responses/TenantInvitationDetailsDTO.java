package tz.tante.rent.manager.models.dtos.responses;

import tz.tante.rent.manager.enums.TenantInvitationStatus;
import java.time.LocalDateTime;

public record TenantInvitationDetailsDTO(
  Long leaseId,
  Long id,
  String firstName,
  String lastName,
  String phoneNumber,
  String email,
  String invitationToken,
  TenantInvitationStatus status,
  LocalDateTime expiresAt,
  LocalDateTime acceptedAt,
  LocalDateTime sentAt
) {
}