package tz.tante.rent.manager.messaging.notification.provider.afrikatalking;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AfrikaTalkingSmsRequest(
  String username,
  String message,
  @JsonProperty("phoneNumbers") List<String> phoneNumbers
)
{
}
