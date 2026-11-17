package tz.tante.rent.manager.messaging.notification.provider.beem;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BeemRecipient(
  @JsonProperty("recipient_id") int recipientId,
  @JsonProperty("dest_addr") String destAddr
)
{
}
