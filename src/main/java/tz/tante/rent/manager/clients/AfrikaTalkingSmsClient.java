package tz.tante.rent.manager.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import tz.tante.rent.manager.messaging.notification.provider.afrikatalking.AfrikaTalkingSmsRequest;
import tz.tante.rent.manager.messaging.notification.provider.afrikatalking.AfrikaTalkingSmsResponse;

@FeignClient(
  name = "afrika-talking-http-client",
  url = "${notification.sms.provider.afrika-talking.api.url.bulk}"
)
public interface AfrikaTalkingSmsClient
{
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  AfrikaTalkingSmsResponse sendSms(@RequestHeader("apiKey") String apiKey, @RequestBody AfrikaTalkingSmsRequest request);
}
