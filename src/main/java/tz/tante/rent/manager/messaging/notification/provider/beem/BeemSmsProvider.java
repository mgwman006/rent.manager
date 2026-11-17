package tz.tante.rent.manager.messaging.notification.provider.beem;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import tz.tante.rent.manager.clients.BeemSmsClient;
import tz.tante.rent.manager.exceptions.TanteException;
import tz.tante.rent.manager.messaging.notification.SmsProvider;
import tz.tante.rent.manager.messaging.notification.SmsResponse;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Component
@Primary
@RequiredArgsConstructor
public class BeemSmsProvider implements SmsProvider
{
  private final BeemSmsClient beemSmsClient;

  @Value("${notification.sms.provider.beem.api.username}")
  private String username;

  @Value("${notification.sms.provider.beem.api.password}")
  private String password;

  @Value("${notification.sms.provider.beem.api.sender.id}")
  private String senderId;

  @Override
  public SmsResponse sendSms(String phoneNumber, String message)
  {
    String authorization = "Basic " + Base64.getEncoder()
      .encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));

    BeemSmsRequest request = new BeemSmsRequest(senderId, "", 0, message, List.of(new BeemRecipient(1, phoneNumber)));
    BeemSmsResponse response = beemSmsClient.sendSms(authorization, request);

    if (response == null || response.successful() == null)
    {
      throw new TanteException("Beem SMS response is empty");
    }

    if (Boolean.FALSE.equals(response.successful()))
    {
      throw new TanteException(response.message() != null ? response.message() : "Beem SMS request failed");
    }

    return new SmsResponse(response.requestId(), "SUCCESS", null);
  }
}
