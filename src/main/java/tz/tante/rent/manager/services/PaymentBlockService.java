package tz.tante.rent.manager.services;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import tz.tante.rent.manager.enums.PaymentStatus;
import tz.tante.rent.manager.exceptions.TanteException;
import tz.tante.rent.manager.models.dtos.requests.payments.PaymentTransactionCreateDTO;
import tz.tante.rent.manager.models.entities.PaymentBlock;
import tz.tante.rent.manager.models.entities.PaymentTransaction;
import tz.tante.rent.manager.repositories.PaymentBlockRepository;

import java.time.LocalDateTime;
import java.util.UUID;


@Setter
@Getter
@AllArgsConstructor
@Service
public class PaymentBlockService
{
  private final PaymentBlockRepository paymentBlockRepository;

  public void recordPaymentByLandlord(Long rentalprofileId, PaymentTransactionCreateDTO paymentTransactionCreateDTO) {
    PaymentBlock paymentBlock = paymentBlockRepository.findById(paymentTransactionCreateDTO.paymentBlockId())
      .orElseThrow(() -> new TanteException("Payment block not found"));

    if (paymentBlock.getAmount().compareTo(paymentTransactionCreateDTO.amount()) < 0) {
      throw new TanteException("Payment amount exceeds the payment block amount");
    }
    if (paymentBlock.getAmount().compareTo(paymentTransactionCreateDTO.amount()) > 0) {
      throw new TanteException("Payment amount is less than the payment block amount");
    }

    PaymentTransaction paymentTransaction = PaymentTransaction.builder()
      .paymentBlock(paymentBlock)
      .amount(paymentTransactionCreateDTO.amount())
      .currency(paymentTransactionCreateDTO.currency())
      .payerUserId(paymentTransactionCreateDTO.payerUserId())
      .note(paymentTransactionCreateDTO.note())
      .transactionDate(LocalDateTime.now())
      .reference(paymentTransactionCreateDTO.reference())
      .method(paymentTransactionCreateDTO.method())
      .status(PaymentStatus.SUCCESSFUL)
      .build();

    paymentBlock.addTransaction(paymentTransaction);
    paymentBlockRepository.save(paymentBlock);
  }

  public void recordPaymentByTenant(Long tenantId, PaymentTransactionCreateDTO paymentTransactionCreateDTO) {
    PaymentBlock paymentBlock = paymentBlockRepository.findById(paymentTransactionCreateDTO.paymentBlockId())
      .orElseThrow(() -> new TanteException("Payment block not found"));

    if (paymentBlock.getAmount().compareTo(paymentTransactionCreateDTO.amount()) < 0) {
      throw new TanteException("Payment amount exceeds the payment block amount");
    }
    if (paymentBlock.getAmount().compareTo(paymentTransactionCreateDTO.amount()) > 0) {
      throw new TanteException("Payment amount is less than the payment block amount");
    }

    UUID transactionReference = UUID.randomUUID();

    PaymentTransaction paymentTransaction = PaymentTransaction.builder()
      .paymentBlock(paymentBlock)
      .amount(paymentTransactionCreateDTO.amount())
      .currency(paymentTransactionCreateDTO.currency())
      .payerUserId(paymentTransactionCreateDTO.payerUserId())
      .note(paymentTransactionCreateDTO.note())
      .transactionDate(LocalDateTime.now())
      .reference(transactionReference.toString())
      .method(paymentTransactionCreateDTO.method())
      .status(PaymentStatus.SUCCESSFUL)
      .build();

    paymentBlock.addTransaction(paymentTransaction);
    paymentBlockRepository.save(paymentBlock);
  }
}
