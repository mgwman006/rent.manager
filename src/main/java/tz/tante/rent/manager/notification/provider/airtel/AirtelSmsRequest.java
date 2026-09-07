package tz.tante.rent.manager.notification.provider.airtel;

public record AirtelSmsRequest(
  String partnerCode,
  String customerMsisdn,
  AirtelMessage message
)
{
}
