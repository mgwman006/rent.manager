package services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tz.tante.rent.manager.clients.PaymentServiceClient;
import tz.tante.rent.manager.enums.ViewingBookingStatus;
import tz.tante.rent.manager.enums.ViewingPaymentStatus;
import tz.tante.rent.manager.messaging.notification.NotificationService;
import tz.tante.rent.manager.models.dtos.responses.viewings.PaymentIntentDetailsDTO;
import tz.tante.rent.manager.models.entities.ViewingBooking;
import tz.tante.rent.manager.repositories.RentalProfileRepository;
import tz.tante.rent.manager.repositories.ViewingBookingRepository;
import tz.tante.rent.manager.repositories.ViewingOutcomeRepository;
import tz.tante.rent.manager.services.ViewingRefundNotifier;
import tz.tante.rent.manager.services.ViewingService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ViewingPaymentCallbackTest
{
  @Mock private ViewingBookingRepository viewingBookingRepository;
  @Mock private ViewingOutcomeRepository viewingOutcomeRepository;
  @Mock private RentalProfileRepository rentalProfileRepository;
  @Mock private NotificationService notificationService;
  @Mock private ViewingRefundNotifier viewingRefundNotifier;
  @Mock private PaymentServiceClient paymentServiceClient;

  private ViewingService viewingService;

  @BeforeEach
  void setUp()
  {
    viewingService = new ViewingService(
      viewingBookingRepository,
      viewingOutcomeRepository,
      rentalProfileRepository,
      notificationService,
      viewingRefundNotifier,
      paymentServiceClient,
      "https://api.tante.tz/rent-manager"
    );
  }

  @Test
  void paidCallbackMovesDraftToPending()
  {
    ViewingBooking booking = booking();
    when(viewingBookingRepository.findByReference("VB-123")).thenReturn(Optional.of(booking));
    when(viewingBookingRepository.save(booking)).thenReturn(booking);

    viewingService.handlePaymentIntentCallback(intent("PAID"));

    assertEquals(ViewingBookingStatus.PENDING, booking.getStatus());
    assertEquals(ViewingPaymentStatus.PAID, booking.getPaymentStatus());
  }

  @Test
  void failedCallbackMarksPaymentFailed()
  {
    ViewingBooking booking = booking();
    when(viewingBookingRepository.findByReference("VB-123")).thenReturn(Optional.of(booking));
    when(viewingBookingRepository.save(booking)).thenReturn(booking);

    viewingService.handlePaymentIntentCallback(intent("FAILED"));

    assertEquals(ViewingPaymentStatus.FAILED, booking.getPaymentStatus());
  }

  @Test
  void duplicatePaidCallbackIsIdempotent()
  {
    ViewingBooking booking = booking();
    booking.setStatus(ViewingBookingStatus.PENDING);
    booking.setPaymentStatus(ViewingPaymentStatus.PAID);
    when(viewingBookingRepository.findByReference("VB-123")).thenReturn(Optional.of(booking));

    viewingService.handlePaymentIntentCallback(intent("SUCCESS"));

    assertEquals(ViewingBookingStatus.PENDING, booking.getStatus());
  }

  private ViewingBooking booking()
  {
    ViewingBooking booking = new ViewingBooking();
    booking.setReference("VB-123");
    booking.setStatus(ViewingBookingStatus.DRAFT);
    booking.setPaymentStatus(ViewingPaymentStatus.AWAITING_PAYMENT);
    booking.setServiceFee(BigDecimal.valueOf(5000));
    return booking;
  }

  private PaymentIntentDetailsDTO intent(String status)
  {
    return new PaymentIntentDetailsDTO(
      1L,
      10L,
      "VIEWING_FEE",
      "VB-123",
      BigDecimal.valueOf(5000),
      "TZS",
      status,
      null,
      null,
      null,
      false,
      LocalDateTime.now(),
      null,
      null
    );
  }
}
