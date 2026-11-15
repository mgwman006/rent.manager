package tz.tante.rent.manager.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import tz.tante.rent.manager.configs.security.FeignOAuth2Config;
import tz.tante.rent.manager.enums.UnitStatus;

import java.util.Map;

@FeignClient(
  name = "property-manager",
  url = "${property.manager.base.url}",
  configuration = FeignOAuth2Config.class
)
public interface PropertyManagerClient
{
  @GetMapping("/v1/units/{unitId}")
  Map<String, Object> getUnitDetails(@PathVariable("unitId") Long unitId);

  @PatchMapping("/v1/units/{unitId}/status")
  void updateUnitStatus(@PathVariable("unitId") Long unitId, @RequestParam("status") UnitStatus status);
}