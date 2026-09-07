package tz.tante.rent.manager.notification.provider.airtel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import tz.tante.rent.manager.notification.SmsProvider;
import tz.tante.rent.manager.notification.SmsResponse;

@Component
@RequiredArgsConstructor
public class AirtelSmsProvider implements SmsProvider
{
  private final WebClient webClient;


  @Override
  public SmsResponse sendSms(String phoneNumber, String message)
  {

    AirtelSmsRequest request = new AirtelSmsRequest("kuunda", phoneNumber, new AirtelMessage(message, null, null));

    AirtelSmsResponse response = webClient
      .post()
      .uri("https://openapiuat.airtel.co.tz/arch-in/web/callback/loans/notify/{partnerCode}", "kuunda")
      .bodyValue(request)
      .retrieve()
      .bodyToMono(AirtelSmsResponse.class)
      .block();

    return new SmsResponse(response.msgid(), String.valueOf(response.st()), response.msg());
  }
}