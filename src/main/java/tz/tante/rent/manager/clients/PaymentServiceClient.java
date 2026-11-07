package tz.tante.rent.manager.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tz.tante.rent.manager.configs.security.FeignOAuth2Config;

@FeignClient(
  name = "tante-pay",
  url = "${tante.pay.base.url}",
  configuration = FeignOAuth2Config.class
)
public interface PaymentServiceClient {
  @PostMapping("/v1/payments/bookings/{bookingReference}/refunds")
  void requestRefund(@PathVariable("bookingReference") String bookingReference, @RequestParam("reason") String reason);
}
