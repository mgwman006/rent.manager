package tz.tante.rent.manager.models.dtos.responses;

import tz.tante.rent.manager.enums.LeaseInvitationStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record LeaseInvitationDetailsDTO(
  Long leaseId,
  Long id,
  String firstName,
  String lastName,
  String phoneNumber,
  String email,
  UUID invitationToken,
  LeaseInvitationStatus status,
  LocalDateTime expiresAt,
  LocalDateTime acceptedAt,
  LocalDateTime sentAt
) {
}