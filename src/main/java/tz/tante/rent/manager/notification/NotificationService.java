package tz.tante.rent.manager.notification;

import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Service
@Setter
@AllArgsConstructor
public class NotificationService
{
  private final SmsProvider smsProvider;


  public SmsResponse sendTenantInvitation(String phoneNumber, String invitationLink)
  {
    String message = String.format(
      "You have been invited to access your rental information on Tante. " +
        "Accept your invitation: %s",
      invitationLink
    );

    return smsProvider.sendSms(phoneNumber, message);
  }
}
