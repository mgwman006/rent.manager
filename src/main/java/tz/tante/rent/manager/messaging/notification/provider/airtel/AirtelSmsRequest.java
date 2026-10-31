package tz.tante.rent.manager.messaging.notification.provider.airtel;

public record AirtelSmsRequest(
  String partnerCode,
  String customerMsisdn,
  AirtelMessage message
)
{
}
