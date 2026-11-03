package tz.tante.rent.manager.messaging.models;

public record NotificationDTO(
  String title,
  String message,
  String type,
  String recipientPhoneNumber,
  String recipientEmail,
  String id
)
{
}