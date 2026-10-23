package services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import tz.tante.rent.manager.enums.LeaseInitiator;
import tz.tante.rent.manager.enums.LeaseStatus;
import tz.tante.rent.manager.enums.PaymentMethod;
import tz.tante.rent.manager.events.RentPaymentRecordedEvent;
import tz.tante.rent.manager.exceptions.TanteException;
import tz.tante.rent.manager.models.dtos.requests.payments.PaymentTransactionCreateDTO;
import tz.tante.rent.manager.models.entities.Lease;
import tz.tante.rent.manager.models.entities.PaymentBlock;
import tz.tante.rent.manager.repositories.PaymentBlockRepository;
import tz.tante.rent.manager.services.PaymentBlockService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentBlockServiceTest
{
  @Mock
  private PaymentBlockRepository paymentBlockRepository;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  private PaymentBlockService paymentBlockService;

  @BeforeEach
  public void setUp()
  {
    paymentBlockService = new PaymentBlockService(paymentBlockRepository, eventPublisher);
  }

  @Test
  public void testRecordPayment_when_payment_block_not_exist_throw_exception()
  {
    PaymentTransactionCreateDTO paymentTransactionCreateDTO = mock(PaymentTransactionCreateDTO.class);
    when(paymentTransactionCreateDTO.paymentBlockId()).thenReturn(999L);
    when(paymentBlockRepository.findById(999L)).thenReturn(Optional.empty());
    assertThrows(TanteException.class, () -> paymentBlockService.recordPaymentByLandlord(1L, paymentTransactionCreateDTO));

    verify(paymentBlockRepository).findById(999L);
    verify(paymentBlockRepository, never()).save(any());
    verifyNoInteractions(eventPublisher);
  }

  @Test
  public void testRecordPayment_whenPaymentBlockExists_savesAndPublishesEvent() {
    PaymentTransactionCreateDTO dto = new PaymentTransactionCreateDTO(
      10L,
      new BigDecimal("500000.00"),
      "TZS",
      20L,
      "October rent",
      "REF-001",
      PaymentMethod.CASH
    );

    Lease lease = Lease.builder()
      .referenceNumber("LEASE-001")
      .startDate(LocalDate.of(2026, 10, 1))
      .endDate(LocalDate.of(2027, 9, 30))
      .fullLeasePaymentRequired(false)
      .status(LeaseStatus.ACTIVE)
      .initiatedBy(LeaseInitiator.values()[0])
      .build();

    // The ID is inherited from BaseEntity, so set it using the setter.
    lease.setId(101L);

    PaymentBlock paymentBlock = PaymentBlock.builder()
      .lease(lease)
      .amount(new BigDecimal("500000.00"))
      .startDate(LocalDate.of(2026, 10, 1))
      .endDate(LocalDate.of(2026, 10, 31))
      .dueDate(LocalDate.of(2026, 10, 5))
      .build();

    when(paymentBlockRepository.findById(10L))
      .thenReturn(Optional.of(paymentBlock));

    paymentBlockService.recordPaymentByLandlord(1L, dto);

    verify(paymentBlockRepository).save(paymentBlock);
    verify(eventPublisher).publishEvent(
      new RentPaymentRecordedEvent(101L)
    );
    Assertions.assertEquals(1, paymentBlock.getTransactions().size());
  }

  @Test
  public void testRecordPayment_whenAmountExceedsPaymentBlock_throwsException() {
    PaymentTransactionCreateDTO dto = new PaymentTransactionCreateDTO(
      10L,
      new BigDecimal("600000.00"),
      "TZS",
      20L,
      "October rent",
      "REF-002",
      PaymentMethod.CASH
    );

    PaymentBlock paymentBlock = PaymentBlock.builder()
      .amount(new BigDecimal("500000.00"))
      .build();

    when(paymentBlockRepository.findById(10L))
      .thenReturn(Optional.of(paymentBlock));

    assertThrows(
      TanteException.class,
      () -> paymentBlockService.recordPaymentByLandlord(1L, dto)
    );

    verify(paymentBlockRepository).findById(10L);
    verify(paymentBlockRepository, never()).save(any());
    verifyNoInteractions(eventPublisher);
  }

  @Test
  public void testRecordPayment_whenAmountIsLessThanPaymentBlock_throwsException() {
    PaymentTransactionCreateDTO dto = new PaymentTransactionCreateDTO(
      10L,
      new BigDecimal("400000.00"),
      "TZS",
      20L,
      "October rent",
      "REF-003",
      PaymentMethod.CASH
    );

    PaymentBlock paymentBlock = PaymentBlock.builder()
      .amount(new BigDecimal("500000.00"))
      .build();

    when(paymentBlockRepository.findById(10L))
      .thenReturn(Optional.of(paymentBlock));

    assertThrows(
      TanteException.class,
      () -> paymentBlockService.recordPaymentByLandlord(1L, dto)
    );

    verify(paymentBlockRepository).findById(10L);
    verify(paymentBlockRepository, never()).save(any());
    verifyNoInteractions(eventPublisher);
  }
}
