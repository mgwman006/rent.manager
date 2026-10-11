package tz.tante.rent.manager.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.tante.rent.manager.clients.PropertyHttpClient;
import tz.tante.rent.manager.enums.LeaseInvitationStatus;
import tz.tante.rent.manager.enums.LeaseStatus;
import tz.tante.rent.manager.enums.UnitStatus;
import tz.tante.rent.manager.models.entities.Lease;
import tz.tante.rent.manager.models.entities.LeaseInvitation;
import tz.tante.rent.manager.repositories.LeaseInvitationRepository;
import tz.tante.rent.manager.repositories.LeaseRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@AllArgsConstructor
public class LeaseInvitationStatusUpdateService
{
  private final LeaseRepository leaseRepository;
  private final LeaseInvitationRepository leaseInvitationRepository;
  private final PropertyHttpClient propertyHttpClient;

  @Transactional
  public void updateLeaseAndUnitStatus(LeaseInvitation invitation, Lease lease)
  {
    lease.setStatus(LeaseStatus.ACTIVE);
    leaseRepository.save(lease);

    invitation.setStatus(LeaseInvitationStatus.ACCEPTED);
    invitation.setAcceptedAt(LocalDateTime.now(ZoneId.of("UTC")));
    leaseInvitationRepository.save(invitation);

    propertyHttpClient.updateUnitStatus(lease.getUnitId(), UnitStatus.OCCUPIED);
  }
}
