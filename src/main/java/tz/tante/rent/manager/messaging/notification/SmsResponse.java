package tz.tante.rent.manager.messaging.notification;

public record SmsResponse(
  String messageId,
  String status,
  String errorMessage)
{
}
