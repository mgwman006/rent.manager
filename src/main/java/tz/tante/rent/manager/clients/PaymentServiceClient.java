package tz.tante.rent.manager.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import tz.tante.rent.manager.configs.security.FeignOAuth2Config;
import tz.tante.rent.manager.models.dtos.ApiResponse;
import tz.tante.rent.manager.models.dtos.requests.viewings.CreateGenericPaymentIntentDTO;
import tz.tante.rent.manager.models.dtos.responses.viewings.PaymentIntentDetailsDTO;

@FeignClient(
  name = "tante-pay",
  url = "${tante.pay.base.url}",
  configuration = FeignOAuth2Config.class
)
public interface PaymentServiceClient {
  @PostMapping("/v1/payments/bookings/{bookingReference}/refunds")
  void requestRefund(@PathVariable("bookingReference") String bookingReference, @RequestParam("reason") String reason);

  @GetMapping("/v1/payments/intents")
  ApiResponse<PaymentIntentDetailsDTO> generatePaymentIntent(@RequestBody CreateGenericPaymentIntentDTO dto);
}
