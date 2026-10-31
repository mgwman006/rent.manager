package tz.tante.rent.manager.messaging.notification.provider.afrikatalking;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SmsMessageData(
  @JsonProperty("Message") String message,
  @JsonProperty("Recipients") List<Recipient> recipients
)
{
}
