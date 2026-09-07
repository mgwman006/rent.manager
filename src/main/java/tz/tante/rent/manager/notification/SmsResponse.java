package tz.tante.rent.manager.notification;

public record SmsResponse(
  String messageId,
  String status,
  String errorMessage)
{
}
