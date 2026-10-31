package tz.tante.rent.manager.services;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


import tz.tante.rent.manager.enums.LeaseInvitationStatus;
import tz.tante.rent.manager.messaging.notification.provider.afrikatalking.AfrikaTalkingSmsProvider;
import tz.tante.rent.manager.models.entities.LeaseInvitation;
import tz.tante.rent.manager.repositories.LeaseInvitationRepository;

@Service
@RequiredArgsConstructor
public class LeaseInvitationSyncService
{
  private final LeaseInvitationRepository leaseInvitationRepository;
  private final AfrikaTalkingSmsProvider smsProvider;


  @Scheduled(fixedDelay = 300000)
  @Transactional
  public void processPendingInvitations()
  {
    List<LeaseInvitation> invitations = leaseInvitationRepository.findByStatus(LeaseInvitationStatus.PENDING);

    for (LeaseInvitation leaseInvitation : invitations)
    {
      leaseInvitation.setStatus(LeaseInvitationStatus.SENT);
      smsProvider.sendSms(leaseInvitation.getPhoneNumber(), "Your lease invitation status has been updated.");
      leaseInvitationRepository.save(leaseInvitation);
    }
  }
}
