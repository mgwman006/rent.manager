package tz.tante.rent.manager.notification.provider.airtel;


public record AirtelSmsResponse(
  boolean st,
  String msg,
  String msgid,
  String timestamp,
  String devErrorMsg,
  Object validationErrors
) {}

