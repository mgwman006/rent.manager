package tz.tante.rent.manager.messaging.notification;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.tante.rent.manager.messaging.models.NotificationDTO;
import tz.tante.rent.manager.messaging.notification.entities.Notification;
import tz.tante.rent.manager.messaging.notification.enums.NotificationStatus;
import tz.tante.rent.manager.messaging.notification.enums.NotificationType;
import tz.tante.rent.manager.repositories.NotificationRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class NotificationService
{
  private final NotificationRepository notificationRepository;

  @Transactional
  public Notification createPendingNotification(NotificationDTO notificationDTO)
  {
    if (StringUtils.isBlank(notificationDTO.title()) || StringUtils.isBlank(notificationDTO.message()) || notificationDTO.type() == null || notificationDTO.type() == NotificationType.INVALID)
    {
      throw new IllegalArgumentException("Notification title, message and type are required.");
    }

    if (StringUtils.isBlank(notificationDTO.recipientPhoneNumber()) && StringUtils.isBlank(notificationDTO.recipientEmail()))
    {
      throw new IllegalArgumentException("Notification must include a phone number or email address.");
    }

    Notification notification = new Notification();
    notification.setTitle(notificationDTO.title());
    notification.setMessage(notificationDTO.message());
    notification.setType(notificationDTO.type());
    notification.setRecipientPhoneNumber(notificationDTO.recipientPhoneNumber());
    notification.setRecipientEmail(notificationDTO.recipientEmail());
    notification.setReferenceType(notificationDTO.referenceType());
    notification.setReferenceId(notificationDTO.referenceId());
    notification.setStatus(NotificationStatus.PENDING);
    notification.setAttempts(0);
    notification.setLastError(null);
    notification.setNextRetryAt(LocalDateTime.now(ZoneId.of("UTC")));
    return notificationRepository.save(notification);
  }
}
