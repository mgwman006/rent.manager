package tz.tante.rent.manager.services;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.tante.rent.manager.enums.LeaseInitiator;
import tz.tante.rent.manager.enums.LeaseInvitationStatus;
import tz.tante.rent.manager.messaging.notification.SmsProvider;
import tz.tante.rent.manager.messaging.notification.SmsResponse;
import tz.tante.rent.manager.models.entities.LeaseInvitation;
import tz.tante.rent.manager.repositories.LeaseInvitationRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaseInvitationSyncService
{
  private final LeaseInvitationRepository leaseInvitationRepository;
  private final SmsProvider smsProvider;

  @Value("${app.tenant.base-url}")
  private String tenantAppBaseUrl;

  @Value("${app.landlord.base-url}")
  private String landlordAppBaseUrl;

  @Value("${app.tenant.invitation-path:/invitations}")
  private String tenantInvitationPath;

  @Value("${app.landlord.invitation-path:/rental-profile/invitations}")
  private String landlordInvitationPath;

  @Deprecated
  @Transactional
  public void processPendingInvitations()
  {
    // Notification dispatch is handled asynchronously by NotificationDispatchService.
  }

  private String buildInvitationMessage(LeaseInvitation invitation)
  {
    boolean isTenant = isTenantInvitation(invitation);
    String baseUrl = isTenant ? tenantAppBaseUrl : landlordAppBaseUrl;
    String invitationPath = isTenant ? tenantInvitationPath : landlordInvitationPath;
    String reviewUrl = baseUrl + invitationPath + "/" + invitation.getToken();

    return String.format(
      "Hi %s %s, you have a lease invitation for %s review. View it here: %s",
      invitation.getFirstName(),
      invitation.getLastName(),
      isTenant ? "tenant" : "landlord",
      reviewUrl
    );
  }

  private boolean isTenantInvitation(LeaseInvitation invitation)
  {
    return invitation.getLease() != null
      && invitation.getLease().getInitiatedBy() == LeaseInitiator.LANDLORD;
  }

  private void handleFailure(LeaseInvitation leaseInvitation, String errorMessage, Exception exception)
  {
    leaseInvitation.setAttempts(leaseInvitation.getAttempts() + 1);
    leaseInvitation.setLastError(errorMessage);

    if (exception instanceof FeignException feignException && feignException.status() >= 400 &&
      feignException.status() < 500 && feignException.status() != 429)
    {
      leaseInvitation.setStatus(LeaseInvitationStatus.FAILED);
      leaseInvitation.setNextRetryAt(null);
      leaseInvitationRepository.save(leaseInvitation);
      log.error("Lease invitation send failed permanently: invitationId={}, phoneNumber={}, HTTP status={}, error={}",
        leaseInvitation.getId(), leaseInvitation.getPhoneNumber(), feignException.status(), errorMessage);
    }
    else
    {
      long delaySeconds = Math.min(30L * (1L << Math.min(leaseInvitation.getAttempts() - 1, 7)), 3600L);
      leaseInvitation.setStatus(LeaseInvitationStatus.PENDING);
      leaseInvitation.setNextRetryAt(LocalDateTime.now(ZoneId.of("UTC")).plusSeconds(delaySeconds));
      leaseInvitationRepository.save(leaseInvitation);
      log.warn("Lease invitation send retry scheduled: invitationId={}, phoneNumber={}, attempts={}, nextRetryAt={}, error={}",
        leaseInvitation.getId(), leaseInvitation.getPhoneNumber(), leaseInvitation.getAttempts(),
        leaseInvitation.getNextRetryAt(), errorMessage);
    }
  }
}
