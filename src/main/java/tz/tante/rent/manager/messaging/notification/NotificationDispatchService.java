package tz.tante.rent.manager.messaging.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.tante.rent.manager.messaging.notification.entities.Notification;
import tz.tante.rent.manager.messaging.notification.enums.NotificationStatus;
import tz.tante.rent.manager.repositories.NotificationRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationDispatchService
{
  private final NotificationRepository notificationRepository;
  private final SmsProvider smsProvider;

  @Scheduled(fixedDelay = 60000)
  @Transactional
  public void processPendingNotifications()
  {
    LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
    List<Notification> notifications = notificationRepository.findByStatusOrderByCreatedAtAsc(NotificationStatus.PENDING)
      .stream()
      .filter(notification -> notification.getNextRetryAt() == null || !notification.getNextRetryAt().isAfter(now))
      .toList();

    for (Notification notification : notifications)
    {
      if (notification.getRecipientPhoneNumber() == null || notification.getRecipientPhoneNumber().isBlank())
      {
        notification.setStatus(NotificationStatus.FAILED);
        notification.setLastError("Notification recipient phone number is missing.");
        notificationRepository.save(notification);
        continue;
      }

      try
      {
        SmsResponse response = smsProvider.sendSms(notification.getRecipientPhoneNumber(), notification.getMessage());
        notification.setStatus(NotificationStatus.SENT);
        notification.setProviderReference(response.messageId());
        notification.setSentAt(LocalDateTime.now(ZoneId.of("UTC")));
        notification.setAttempts(notification.getAttempts() + 1);
        notification.setLastError(null);
        notification.setNextRetryAt(null);
        notificationRepository.save(notification);
      }
      catch (Exception exception)
      {
        notification.setAttempts(notification.getAttempts() + 1);
        notification.setLastError(exception.getMessage());

        if (notification.getAttempts() >= 5)
        {
          notification.setStatus(NotificationStatus.FAILED);
          notification.setNextRetryAt(null);
        }
        else
        {
          notification.setStatus(NotificationStatus.PENDING);
          notification.setNextRetryAt(LocalDateTime.now(ZoneId.of("UTC")).plusMinutes((long) Math.pow(2, notification.getAttempts() - 1)));
        }

        notificationRepository.save(notification);
        log.warn("Notification dispatch failed: notificationId={}, type={}, attempts={}, error={}",
          notification.getId(), notification.getType(), notification.getAttempts(), exception.getMessage());
      }
    }
  }
}
