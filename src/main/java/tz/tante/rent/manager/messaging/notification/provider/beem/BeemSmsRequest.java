package tz.tante.rent.manager.messaging.notification.provider.beem;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record BeemSmsRequest(
  @JsonProperty("source_addr") String sourceAddr,
  @JsonProperty("schedule_time") String scheduleTime,
  int encoding,
  String message,
  List<BeemRecipient> recipients
)
{
}
