package tz.tante.rent.manager.services;

import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.tante.rent.manager.enums.LeaseStatus;
import tz.tante.rent.manager.enums.LeaseInvitationStatus;
import tz.tante.rent.manager.exceptions.ResourceExistException;
import tz.tante.rent.manager.exceptions.ResourceNotFoundException;
import tz.tante.rent.manager.models.dtos.requests.leaseinvitation.LeaseInvitationCreateDTO;
import tz.tante.rent.manager.models.dtos.responses.LeaseInvitationDetailsDTO;
import tz.tante.rent.manager.models.entities.Lease;
import tz.tante.rent.manager.models.entities.Tenant;
import tz.tante.rent.manager.models.entities.LeaseInvitation;
import tz.tante.rent.manager.repositories.LeaseRepository;
import tz.tante.rent.manager.repositories.LeaseInvitationRepository;
import tz.tante.rent.manager.repositories.TenantRepository;
import static tz.tante.rent.manager.utilities.Constant.TENANT_NOT_FOUND_BY_TOKEN_MESSAGE;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;


@Service
@AllArgsConstructor
@Setter
public class LeaseInvitationService
{
  private final LeaseInvitationRepository leaseInvitationRepository;
  private final LeaseRepository leaseRepository;
  private final TenantRepository tenantRepository;

  public LeaseInvitationDetailsDTO createInvitation(LeaseInvitationCreateDTO request)
  {
    Lease lease = leaseRepository.findById(request.leaseId())
      .orElseThrow(() -> new ResourceNotFoundException("Lease not found with id: " + request.leaseId()));

    if (lease.getStatus() != LeaseStatus.PENDING || lease.getTenantId() != null)
    {
      throw new ResourceExistException("Cannot create tenant invitation for a lease that is not in a pending state or already has a tenant assigned.");
    }

    LeaseInvitation leaseInvitation = new LeaseInvitation();
    leaseInvitation.setFirstName(request.firstName());
    leaseInvitation.setLastName(request.lastName());
    leaseInvitation.setPhoneNumber(request.phoneNumber());
    leaseInvitation.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC")));
    leaseInvitation.setExpiresAt(LocalDateTime.now(ZoneId.of("UTC")).plusDays(7)); // Set expiration date for the invitation
    leaseInvitation.setStatus(LeaseInvitationStatus.PENDING);
    lease.addInvitation(leaseInvitation);

    leaseInvitation = leaseInvitationRepository.save(leaseInvitation);

    return mapInvitationToDTO(lease.getId(), leaseInvitation);
  }

  @Transactional
  public void tenantAcceptInvitationInitiatedByLandlord(UUID invitationToken, Long userId)
  {
    LeaseInvitation invitation = leaseInvitationRepository.findByToken(invitationToken)
      .orElseThrow(() -> new ResourceNotFoundException(TENANT_NOT_FOUND_BY_TOKEN_MESSAGE + invitationToken));

    if (invitation.getStatus() != LeaseInvitationStatus.PENDING)
    {
      throw new ResourceExistException("Tenant invitation has already been processed.");
    }

    Lease lease = invitation.getLease();
    if (lease.getStatus() != LeaseStatus.PENDING || lease.getTenantId() != null)
    {
      throw new ResourceExistException("Lease is not in a pending state"+(lease.getTenantId() != null ? " and tenant already assigned." : ""));
    }

    Tenant tenant = tenantRepository.findByUserId(userId).orElse(null);
    if (tenant == null)
    {
      try
      {
        tenant = new Tenant();
        tenant.setUserId(userId);
        tenant.setPhoneNumber(invitation.getPhoneNumber());
        tenant.setEmail(invitation.getEmail());
        tenant.setFirstName(invitation.getFirstName());
        tenant.setLastName(invitation.getLastName());
        tenant = tenantRepository.save(tenant);
      }
      catch (DataIntegrityViolationException ex)
      {
        tenant = tenantRepository.findByUserId(userId)
          .orElseThrow(() -> ex);
      }
    }


    lease.setTenantId(tenant.getId());
    lease.setStatus(LeaseStatus.ACTIVE);
    leaseRepository.save(lease);

    invitation.setStatus(LeaseInvitationStatus.ACCEPTED);
    invitation.setAcceptedAt(LocalDateTime.now(ZoneId.of("UTC")));
    leaseInvitationRepository.save(invitation);
  }

  private LeaseInvitationDetailsDTO mapInvitationToDTO(Long leaseId, LeaseInvitation invitation)
  {
    return new LeaseInvitationDetailsDTO(
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

  public LeaseInvitationDetailsDTO getInvitationDetails(UUID invitationToken)
  {
    LeaseInvitation invitation = leaseInvitationRepository.findByToken(invitationToken)
      .orElseThrow(() -> new ResourceNotFoundException(TENANT_NOT_FOUND_BY_TOKEN_MESSAGE + invitationToken));

    return mapInvitationToDTO(invitation.getLease().getId(), invitation);
  }

  public List<LeaseInvitationDetailsDTO> getPendingInvitationsByPhoneNumber(String phoneNumber)
  {
    List<LeaseInvitation> invitations = leaseInvitationRepository.findByPhoneNumberAndStatus(phoneNumber, LeaseInvitationStatus.PENDING);
    return invitations.stream()
      .map(invitation -> mapInvitationToDTO(invitation.getLease().getId(), invitation))
      .toList();
  }
}
