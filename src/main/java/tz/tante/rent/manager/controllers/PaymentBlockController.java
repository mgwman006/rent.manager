package tz.tante.rent.manager.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.tante.rent.manager.models.dtos.ApiResponse;
import tz.tante.rent.manager.models.dtos.requests.payments.PaymentTransactionCreateDTO;
import tz.tante.rent.manager.services.PaymentBlockService;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/payment-blocks")
public class PaymentBlockController
{
  private final PaymentBlockService paymentBlockService;

  @PostMapping("/rental-profile/{rentalprofileId}/record-payment")
  public ResponseEntity<ApiResponse<String>> recordPaymentByLandlord(@PathVariable Long rentalprofileId,
                                                                     @RequestBody PaymentTransactionCreateDTO paymentTransactionCreateDTO) {
    paymentBlockService.recordPaymentByLandlord(rentalprofileId, paymentTransactionCreateDTO);
    return ResponseEntity.ok(ApiResponse.success("Payment recorded successfully", 200));
  }
}
