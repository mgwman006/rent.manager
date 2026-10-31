package tz.tante.rent.manager.messaging.notification.provider.afrikatalking;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AfrikaTalkingSmsResponse(
  @JsonProperty("SMSMessageData") SmsMessageData smsMessageData
)
{
}
