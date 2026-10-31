package tz.tante.rent.manager.messaging.notification;

public interface SmsProvider
{
  SmsResponse sendSms(String phoneNumber, String message);
}
