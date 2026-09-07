package tz.tante.rent.manager.notification;

public interface SmsProvider
{
  SmsResponse sendSms(String phoneNumber, String message);
}
