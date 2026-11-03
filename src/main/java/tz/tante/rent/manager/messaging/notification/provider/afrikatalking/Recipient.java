package tz.tante.rent.manager.messaging.notification.provider.afrikatalking;

public record Recipient(
  String cost,
  String messageId,
  int messageParts,
  String number,
  String status,
  int statusCode
)
{
}
