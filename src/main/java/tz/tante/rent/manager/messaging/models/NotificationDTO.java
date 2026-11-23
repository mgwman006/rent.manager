package tz.tante.rent.manager.messaging.models;

import tz.tante.rent.manager.messaging.notification.enums.NotificationType;

public record NotificationDTO(
  String title,
  String message,
  NotificationType type,
  String recipientPhoneNumber,
  String recipientEmail,
  String referenceId,
  String referenceType)
{
}