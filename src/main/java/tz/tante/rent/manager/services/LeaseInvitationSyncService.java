package tz.tante.rent.manager.services;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tz.tante.rent.manager.enums.LeaseInvitationStatus;
import tz.tante.rent.manager.messaging.RabbitMQPublisher;
import tz.tante.rent.manager.messaging.models.NotificationDTO;
import tz.tante.rent.manager.models.entities.LeaseInvitation;
import tz.tante.rent.manager.repositories.LeaseInvitationRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaseInvitationSyncService
{
  private final LeaseInvitationRepository leaseInvitationRepository;
  private final RabbitMQPublisher publisher;

  @Scheduled(fixedDelay = 300000)
  public void processPendingInvitations()
  {
    List<LeaseInvitation> invitation = leaseInvitationRepository.findByStatus(LeaseInvitationStatus.PENDING);

    for (LeaseInvitation leaseInvitation : invitation)
    {
      NotificationDTO notification = new NotificationDTO(
        "lease-invitation-status-update",
        "Lease Invitation Status Update",
        "SMS",
        leaseInvitation.getPhoneNumber(),
        String.valueOf(leaseInvitation.getId())
      );
      leaseInvitation.setStatus(LeaseInvitationStatus.SENT);
      publisher.publish(notification);
      leaseInvitationRepository.save(leaseInvitation);
    }
  }
}
