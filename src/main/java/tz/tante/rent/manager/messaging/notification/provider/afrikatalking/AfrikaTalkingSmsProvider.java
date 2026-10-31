package tz.tante.rent.manager.messaging.notification.provider.afrikatalking;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import tz.tante.rent.manager.clients.AfrikaTalkingSmsClient;
import tz.tante.rent.manager.messaging.notification.SmsProvider;
import tz.tante.rent.manager.messaging.notification.SmsResponse;

import java.util.List;

@Component
@Primary
@RequiredArgsConstructor
public class AfrikaTalkingSmsProvider implements SmsProvider
{
  private final AfrikaTalkingSmsClient afrikaTalkingSmsClient;

  @Value("${notification.sms.provider.afrika-talking.api.key}")
  private String apiKey;

  @Value("${notification.sms.provider.afrika-talking.api.username}")
  private String username;

  @Override
  public SmsResponse sendSms(String phoneNumber, String message)
  {
    AfrikaTalkingSmsRequest request = new AfrikaTalkingSmsRequest(username, message, List.of(phoneNumber));

    try
    {
      AfrikaTalkingSmsResponse response = afrikaTalkingSmsClient.sendSms(apiKey, request);

      if (response == null || response.smsMessageData() == null || response.smsMessageData().recipients() == null || response.smsMessageData().recipients().isEmpty())
      {
        return new SmsResponse(null, "UNKNOWN", "No SMS recipients were returned by AfrikaTalking.");
      }

      Recipient recipient = response.smsMessageData().recipients().getFirst();
      return new SmsResponse(
        recipient.messageId(),
        recipient.status(),
        String.valueOf(recipient.statusCode())
      );
    }
    catch (FeignException feignException)
    {
      return new SmsResponse(null, String.valueOf(feignException.status()), feignException.getMessage());
    }
    catch (Exception exception)
    {
      return new SmsResponse(null, "ERROR", exception.getMessage());
    }
  }
}
