package tz.tante.rent.manager.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import tz.tante.rent.manager.messaging.notification.provider.beem.BeemSmsRequest;
import tz.tante.rent.manager.messaging.notification.provider.beem.BeemSmsResponse;

@FeignClient(
  name = "beem-sms-http-client",
  url = "${notification.sms.provider.beem.api.url}"
)
public interface BeemSmsClient
{
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  BeemSmsResponse sendSms(@RequestHeader("Authorization") String authorization, @RequestBody BeemSmsRequest request);
}
