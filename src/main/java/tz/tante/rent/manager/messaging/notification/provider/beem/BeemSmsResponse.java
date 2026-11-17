package tz.tante.rent.manager.messaging.notification.provider.beem;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BeemSmsResponse(
  @JsonProperty("successful") Boolean successful,
  @JsonProperty("request_id") String requestId,
  @JsonProperty("code") Integer code,
  @JsonProperty("message") String message,
  @JsonProperty("valid") Integer valid,
  @JsonProperty("invalid") Integer invalid,
  @JsonProperty("duplicates") Integer duplicates,
  @JsonProperty("opted_out") Integer optedOut,
  @JsonProperty("total_validated") Integer totalValidated
)
{
}
