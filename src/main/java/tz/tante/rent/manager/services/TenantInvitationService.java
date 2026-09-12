package tz.tante.rent.manager.services;

import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.tante.rent.manager.enums.LeaseStatus;
import tz.tante.rent.manager.enums.TenantInvitationStatus;
import tz.tante.rent.manager.exceptions.ResourceNotFoundException;
import tz.tante.rent.manager.models.dtos.requests.tenantinvitation.TenantInvitationCreateDTO;
import tz.tante.rent.manager.models.dtos.responses.TenantInvitationDetailsDTO;
import tz.tante.rent.manager.models.entities.Lease;
import tz.tante.rent.manager.models.entities.Tenant;
import tz.tante.rent.manager.models.entities.TenantInvitation;
import tz.tante.rent.manager.repositories.LeaseRepository;
import tz.tante.rent.manager.repositories.TenantInvitationRepository;
import tz.tante.rent.manager.repositories.TenantRepository;
import static tz.tante.rent.manager.utilities.Constant.TENANT_NOT_FOUND_BY_TOKEN_MESSAGE;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;


@Service
@AllArgsConstructor
@Setter
public class TenantInvitationService
{
  private final TenantInvitationRepository tenantInvitationRepository;
  private final LeaseRepository leaseRepository;
  private final TenantRepository tenantRepository;

  public TenantInvitationDetailsDTO createTenantInvitation(TenantInvitationCreateDTO request)
  {
    Lease lease = leaseRepository.findById(request.leaseId())
      .orElseThrow(() -> new ResourceNotFoundException("Lease not found with id: " + request.leaseId()));

    TenantInvitation tenantInvitation = new TenantInvitation();
    tenantInvitation.setFirstName(request.firstName());
    tenantInvitation.setLastName(request.lastName());
    tenantInvitation.setPhoneNumber(request.phoneNumber());
    tenantInvitation.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC")));
    tenantInvitation.setExpiresAt(LocalDateTime.now(ZoneId.of("UTC")).plusDays(7)); // Set expiration date for the invitation
    tenantInvitation.setStatus(TenantInvitationStatus.PENDING);
    lease.addTenantInvitation(tenantInvitation);

    tenantInvitation = tenantInvitationRepository.save(tenantInvitation);

    return mapTenantInvitationToDTO(lease.getId(), tenantInvitation);
  }

  @Transactional
  public void acceptTenantInvitation(UUID invitationToken, Long userId)
  {
    TenantInvitation invitation = tenantInvitationRepository.findByToken(invitationToken)
      .orElseThrow(() -> new ResourceNotFoundException(TENANT_NOT_FOUND_BY_TOKEN_MESSAGE + invitationToken));

    Tenant tenant = tenantRepository.findByUserId(userId)
      .orElse(createTenant(
        userId,
        invitation.getPhoneNumber(),
        invitation.getEmail(),
        invitation.getFirstName(),
        invitation.getLastName()));


    Lease lease = invitation.getLease();
    lease.setTenantId(tenant.getId());
    lease.setStatus(LeaseStatus.ACTIVE);
    leaseRepository.save(lease);

    invitation.setStatus(TenantInvitationStatus.ACCEPTED);
    invitation.setAcceptedAt(LocalDateTime.now(ZoneId.of("UTC")));
    tenantInvitationRepository.save(invitation);
  }

  private TenantInvitationDetailsDTO mapTenantInvitationToDTO(Long leaseId, TenantInvitation invitation)
  {
    return new TenantInvitationDetailsDTO(
      leaseId,
      invitation.getId(),
      invitation.getFirstName(),
      invitation.getLastName(),
      invitation.getPhoneNumber(),
      invitation.getEmail(),
      invitation.getToken(),
      invitation.getStatus(),
      invitation.getExpiresAt(),
      invitation.getAcceptedAt() != null ? invitation.getAcceptedAt() : null,
      invitation.getSentAt() != null ? invitation.getSentAt() : null
    );
  }

  private Tenant createTenant(Long userId, String phoneNumber, String email, String firstName, String lastName) {
    Tenant tenant = new Tenant();
    tenant.setUserId(userId);
    tenant.setPhoneNumber(phoneNumber);
    tenant.setEmail(email);
    tenant.setFirstName(firstName);
    tenant.setLastName(lastName);
    return tenantRepository.save(tenant);
  }

  public TenantInvitationDetailsDTO getTenantInvitationDetails(UUID invitationToken)
  {
    TenantInvitation invitation = tenantInvitationRepository.findByToken(invitationToken)
      .orElseThrow(() -> new ResourceNotFoundException(TENANT_NOT_FOUND_BY_TOKEN_MESSAGE + invitationToken));

    return mapTenantInvitationToDTO(invitation.getLease().getId(), invitation);
  }

  public List<TenantInvitationDetailsDTO> getActiveInvitationsByPhoneNumber(String phoneNumber)
  {
    List<TenantInvitation> invitations = tenantInvitationRepository.findByPhoneNumberAndStatus(phoneNumber, TenantInvitationStatus.PENDING);
    return invitations.stream()
      .map(invitation -> mapTenantInvitationToDTO(invitation.getLease().getId(), invitation))
      .toList();
  }
}
