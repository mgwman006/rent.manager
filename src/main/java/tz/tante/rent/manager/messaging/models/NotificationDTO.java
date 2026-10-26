package tz.tante.rent.manager.messaging.models;

public record NotificationDTO(
  String notificationTitle,
  String notificationMessage,
  String notificationType,
  String notificationRecipient,
  String id
) {}