package tz.tante.rent.manager.services;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tz.tante.rent.manager.clients.PaymentServiceClient;
import tz.tante.rent.manager.enums.TanteService;
import tz.tante.rent.manager.enums.ViewingBookingStatus;
import tz.tante.rent.manager.enums.ViewingPaymentStatus;
import tz.tante.rent.manager.exceptions.ResourceNotFoundException;
import tz.tante.rent.manager.exceptions.TanteException;
import tz.tante.rent.manager.messaging.models.NotificationDTO;
import tz.tante.rent.manager.messaging.notification.NotificationService;
import tz.tante.rent.manager.messaging.notification.enums.NotificationType;
import tz.tante.rent.manager.models.dtos.ApiResponse;
import tz.tante.rent.manager.models.dtos.requests.viewings.CreateGenericPaymentIntentDTO;
import tz.tante.rent.manager.models.dtos.requests.viewings.CreateViewingBookingDTO;
import tz.tante.rent.manager.models.dtos.requests.viewings.RecordViewingOutcomeDTO;
import tz.tante.rent.manager.models.dtos.requests.viewings.UpdateViewingPaymentStatusDTO;
import tz.tante.rent.manager.models.dtos.responses.viewings.PaymentIntentDetailsDTO;
import tz.tante.rent.manager.models.dtos.responses.viewings.ViewingBookingDetailsDTO;
import tz.tante.rent.manager.models.dtos.responses.viewings.ViewingOutcomeDetailsDTO;
import tz.tante.rent.manager.models.entities.RentalProfile;
import tz.tante.rent.manager.models.entities.ViewingBooking;
import tz.tante.rent.manager.models.entities.ViewingOutcome;
import tz.tante.rent.manager.repositories.RentalProfileRepository;
import tz.tante.rent.manager.repositories.ViewingBookingRepository;
import tz.tante.rent.manager.repositories.ViewingOutcomeRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class ViewingService
{
  /**
   * Constants
   */
  private static final String NOT_FOUND = " Not found.";
  private static final Set<ViewingBookingStatus> AGENT_HIDDEN_STATUSES = Set.of(
    ViewingBookingStatus.DRAFT,
    ViewingBookingStatus.EXPIRED
  );

  private final ViewingBookingRepository viewingBookingRepository;
  private final ViewingOutcomeRepository viewingOutcomeRepository;
  private final RentalProfileRepository rentalProfileRepository;
  private final NotificationService notificationService;
  private final ViewingRefundNotifier viewingRefundNotifier;
  private final PaymentServiceClient paymentServiceClient;
  private final String publicBaseUrl;

  public ViewingService(
    ViewingBookingRepository viewingBookingRepository,
    ViewingOutcomeRepository viewingOutcomeRepository,
    RentalProfileRepository rentalProfileRepository,
    NotificationService notificationService,
    ViewingRefundNotifier viewingRefundNotifier,
    PaymentServiceClient paymentServiceClient,
    @Value("${rent-manager.public-base-url:http://localhost:8083/rent-manager}") String publicBaseUrl
  ) {
    this.viewingBookingRepository = viewingBookingRepository;
    this.viewingOutcomeRepository = viewingOutcomeRepository;
    this.rentalProfileRepository = rentalProfileRepository;
    this.notificationService = notificationService;
    this.viewingRefundNotifier = viewingRefundNotifier;
    this.paymentServiceClient = paymentServiceClient;
    this.publicBaseUrl = publicBaseUrl;
  }

  @Transactional
  public ViewingBookingDetailsDTO createViewingBooking(CreateViewingBookingDTO request)
  {
    if (request.startTime().isAfter(request.endTime()) || request.startTime().isEqual(request.endTime()))
    {
      throw new TanteException("Viewing start time must be before end time.");
    }

    if (request.serviceFee().compareTo(BigDecimal.ZERO) < 0)
    {
      throw new TanteException("Viewing fee cannot be negative.");
    }

    RentalProfile rentalProfile = rentalProfileRepository.findById(request.rentalProfileId())
      .orElseThrow(() -> new ResourceNotFoundException("Rental profile with ID " + request.rentalProfileId() + NOT_FOUND));

    if (hasActiveBookingConflict(rentalProfile.getId(), request.startTime(), request.endTime()))
    {
      throw new TanteException("No agent is available for the requested time because of an overlapping active viewing.");
    }

    ViewingBooking booking = new ViewingBooking();
    booking.setReference("VB-" + UUID.randomUUID());
    booking.setRentalProfile(rentalProfile);
    booking.setUnitId(request.unitId());
    booking.setStartTime(request.startTime());
    booking.setEndTime(request.endTime());
    booking.setRequesterUserAccountId(request.requesterUserAccountId());
    booking.setServiceFee(request.serviceFee());
    booking.setRequesterPhoneNumber(request.requesterPhoneNumber());
    booking.setNotes(request.notes());
    booking.setStatus(ViewingBookingStatus.DRAFT);
    booking = viewingBookingRepository.save(booking);

    NotificationDTO notificationDTO;
    if (request.serviceFee().compareTo(BigDecimal.ZERO) == 0)
    {
      booking.setStatus(ViewingBookingStatus.PENDING);
      booking.setPaymentStatus(ViewingPaymentStatus.NOT_REQUIRED);

      String msg = """
        You have a new viewing booking. Please sign in to your account to view the details and confirm the booking.
        
        Umepata booking ya kuangalia nyumba. Tafadhali ingia kwenye akaunti yako ili kuona maelezo na kuthibitisha booking.
        """;
      notificationDTO = new NotificationDTO(
        "New viewing booking",
        msg,
        NotificationType.SMS,
        rentalProfile.getPhoneNumber(),
        null,
        "VIEWING_BOOKING",
        null
      );
    }
    else
    {
      PaymentIntentDetailsDTO paymentIntentDetails = generatePaymentIntent(booking);
      String paymentLink = paymentIntentDetails.paymentLink();
      booking.setHoldExpiresAt(paymentIntentDetails.expiresAt());
      booking.setPaymentStatus(ViewingPaymentStatus.AWAITING_PAYMENT);

      booking.setPaymentUrl(paymentLink);
      String msg = String.format(
        "You have a new viewing booking. Click the link to complete payment before %s : %s",
        paymentIntentDetails.expiresAt(),
        paymentLink
      );
      notificationDTO = new NotificationDTO(
        "New viewing booking",
        msg,
        NotificationType.SMS,
        request.requesterPhoneNumber(),
        null,
        booking.getReference(),
        "VIEWING_BOOKING"
      );
    }

    ViewingBooking savedBooking = viewingBookingRepository.save(booking);
    notificationService.createPendingNotification(notificationDTO);
    return toBookingDetails(savedBooking);
  }

  @Transactional
  public ViewingBookingDetailsDTO handlePaymentIntentCallback(PaymentIntentDetailsDTO intent)
  {
    if (intent == null || intent.serviceReferenceNumber() == null || intent.serviceReferenceNumber().isBlank())
    {
      throw new TanteException("Payment callback reference is required.");
    }
    if (intent.status() == null)
    {
      throw new TanteException("Payment callback status is required.");
    }

    ViewingPaymentStatus paymentStatus = mapPaymentIntentStatus(intent.status().name());
    return updatePaymentStatus(
      intent.serviceReferenceNumber(),
      new UpdateViewingPaymentStatusDTO(paymentStatus, null)
    );
  }

  private ViewingPaymentStatus mapPaymentIntentStatus(String status)
  {
    return switch (status.trim().toUpperCase())
    {
      case "PAID", "SUCCESS", "SUCCESSFUL", "SETTLED", "COMPLETED" -> ViewingPaymentStatus.PAID;
      case "FAILED", "FAILURE", "CANCELLED", "CANCELED", "EXPIRED" -> ViewingPaymentStatus.FAILED;
      case "REFUNDED", "REVERSED" -> ViewingPaymentStatus.REFUNDED;
      default -> throw new TanteException("Unsupported payment intent status: " + status);
    };
  }

  @Transactional
  public ViewingBookingDetailsDTO updatePaymentStatus(String bookingReference, UpdateViewingPaymentStatusDTO request)
  {
    ViewingBooking booking = findBookingByReference(bookingReference);

    if (booking.getPaymentStatus() == request.paymentStatus())
    {
      return toBookingDetails(booking);
    }

    switch (request.paymentStatus())
    {
      case PAID -> applyPaymentSucceeded(booking);
      case FAILED -> applyPaymentFailed(booking);
      case REFUNDED -> booking.setPaymentStatus(ViewingPaymentStatus.REFUNDED);
      default -> throw new TanteException("Unsupported viewing payment status update: " + request.paymentStatus());
    }

    booking.setPaymentUpdatedAt(LocalDateTime.now(ZoneId.of("UTC")));
    return toBookingDetails(viewingBookingRepository.save(booking));
  }

  private void applyPaymentSucceeded(ViewingBooking booking)
  {
    if (booking.getStatus() != ViewingBookingStatus.DRAFT)
    {
      throw new TanteException("Only draft viewing bookings can be settled.");
    }

    booking.setPaymentStatus(ViewingPaymentStatus.PAID);
    booking.setStatus(ViewingBookingStatus.PENDING);
    booking.setHoldExpiresAt(null);
  }

  private void applyPaymentFailed(ViewingBooking booking)
  {
    if (booking.getStatus() != ViewingBookingStatus.DRAFT)
    {
      throw new TanteException("Only draft viewing bookings can be marked as failed.");
    }

    booking.setPaymentStatus(ViewingPaymentStatus.FAILED);
    booking.setStatus(ViewingBookingStatus.DRAFT);
  }

  @Transactional
  public int expireStaleDrafts()
  {
    List<ViewingBooking> staleDrafts = viewingBookingRepository.findByStatusAndHoldExpiresAtBefore(ViewingBookingStatus.DRAFT, LocalDateTime.now(ZoneId.of("UTC")));

    staleDrafts.forEach(booking -> {
      booking.setStatus(ViewingBookingStatus.EXPIRED);
      booking.setHoldExpiresAt(null);
      viewingBookingRepository.save(booking);
    });

    return staleDrafts.size();
  }

  public List<ViewingBookingDetailsDTO> getBookingsByRequester(Long requesterUserAccountId)
  {
    return viewingBookingRepository.findByRequesterUserAccountId(requesterUserAccountId).stream()
      .sorted(Comparator.comparing(ViewingBooking::getStartTime).reversed())
      .map(this::toBookingDetails)
      .toList();
  }

  public List<ViewingBookingDetailsDTO> getBookingsByRentalProfile(Long rentalProfileId)
  {
    return viewingBookingRepository.findByRentalProfileIdAndStatusNotIn(rentalProfileId, AGENT_HIDDEN_STATUSES).stream()
      .sorted(Comparator.comparing(ViewingBooking::getStartTime))
      .map(this::toBookingDetails)
      .toList();
  }

  public ViewingBookingDetailsDTO getBooking(Long bookingId)
  {
    return toBookingDetails(findBooking(bookingId));
  }

  public ViewingBookingDetailsDTO getBookingByReference(String reference)
  {
    return toBookingDetails(findBookingByReference(reference));
  }

  @Transactional
  public ViewingBookingDetailsDTO approveBooking(Long bookingId)
  {
    ViewingBooking booking = findBooking(bookingId);

    if (booking.getStatus() != ViewingBookingStatus.PENDING)
    {
      throw new TanteException("Only paid viewing bookings awaiting approval can be approved.");
    }

    booking.setStatus(ViewingBookingStatus.CONFIRMED);
    return toBookingDetails(viewingBookingRepository.save(booking));
  }

  @Transactional
  public ViewingBookingDetailsDTO rejectBooking(Long bookingId)
  {
    ViewingBooking booking = findBooking(bookingId);

    if (booking.getStatus() != ViewingBookingStatus.PENDING)
    {
      throw new TanteException("This viewing booking cannot be rejected from its current status.");
    }

    booking.setStatus(ViewingBookingStatus.REJECTED);
    requestRefundIfPaid(booking, "Viewing booking rejected by the agent");
    return toBookingDetails(viewingBookingRepository.save(booking));
  }

  @Transactional
  public ViewingBookingDetailsDTO cancelBooking(Long bookingId)
  {
    ViewingBooking booking = findBooking(bookingId);

    if (booking.getStatus() != ViewingBookingStatus.CONFIRMED)
    {
      throw new TanteException("This viewing booking cannot be cancelled from its current status.");
    }

    booking.setStatus(ViewingBookingStatus.CANCELLED);
    requestRefundIfPaid(booking, "Viewing booking cancelled");
    return toBookingDetails(viewingBookingRepository.save(booking));
  }

  @Transactional
  public ViewingBookingDetailsDTO recordOutcome(Long bookingId, RecordViewingOutcomeDTO request)
  {
    ViewingBooking booking = findBooking(bookingId);

    if (booking.getStatus() != ViewingBookingStatus.CONFIRMED)
    {
      throw new TanteException("Only confirmed viewing bookings can have an outcome recorded.");
    }

    ViewingOutcome viewingOutcome = new ViewingOutcome();
    viewingOutcome.setBooking(booking);
    viewingOutcome.setType(request.outcome());
    viewingOutcome.setNotes(request.notes());
    viewingOutcome.setRecordedAt(LocalDateTime.now(ZoneId.of("UTC")));
    viewingOutcome = viewingOutcomeRepository.save(viewingOutcome);

    switch (request.outcome()) {
      case COMPLETED -> booking.setStatus(ViewingBookingStatus.CLOSED);
      case AGENT_ABSENT, TENANT_ABSENT, UNIT_NOT_AVAILABLE -> booking.setStatus(ViewingBookingStatus.CANCELLED);
      case RESCHEDULED -> booking.setStatus(ViewingBookingStatus.RESCHEDULED);
      default -> throw new TanteException("Unsupported viewing outcome type: " + request.outcome());
    }

    booking.setOutcome(viewingOutcome);
    if (booking.getStatus() == ViewingBookingStatus.CANCELLED)
    {
      requestRefundIfPaid(booking, "Viewing outcome: " + request.outcome());
    }
    return toBookingDetails(viewingBookingRepository.save(booking));
  }

  private void requestRefundIfPaid(ViewingBooking booking, String reason)
  {
    if (booking.getPaymentStatus() == ViewingPaymentStatus.PAID)
    {
      viewingRefundNotifier.requestRefund(booking.getReference(), reason);
    }
  }

  private ViewingBooking findBooking(Long bookingId)
  {
    return viewingBookingRepository.findById(bookingId)
      .orElseThrow(() -> new ResourceNotFoundException("Viewing booking with ID " + bookingId + " not found."));
  }

  private ViewingBooking findBookingByReference(String reference)
  {
    return viewingBookingRepository.findByReference(reference)
      .orElseThrow(() -> new ResourceNotFoundException("Viewing booking " + reference + " not found."));
  }

  private boolean hasActiveBookingConflict(Long rentalProfileId, LocalDateTime startTime, LocalDateTime endTime)
  {
    LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
    return viewingBookingRepository.findByRentalProfileId(rentalProfileId).stream()
      .filter(booking -> holdsTheSlot(booking, now))
      .anyMatch(booking -> startTime.isBefore(booking.getEndTime()) && endTime.isAfter(booking.getStartTime()));
  }

  private PaymentIntentDetailsDTO generatePaymentIntent(ViewingBooking booking)
  {
    PaymentIntentDetailsDTO intent = null;
    try
    {
      ApiResponse<PaymentIntentDetailsDTO> response = paymentServiceClient.generatePaymentIntent(
        new CreateGenericPaymentIntentDTO(
          booking.getRequesterUserAccountId(),
          booking.getServiceFee(),
          "TZS",
          TanteService.VIEWING_BOOKING,
          booking.getReference(),
          generateCallbackUrl(booking.getReference())
        )
      );
      intent = unwrapResponse(response, "Unable to create payment intent.");
    }
    catch (Exception ex)
    {
      log.warn("Unable to fetch tantepay payment URL for booking {}: {}", booking.getReference(), ex.getMessage());
    }
    return intent;
  }

  private String generateCallbackUrl(String bookingReference)
  {
    return publicBaseUrl + "/v1/viewings/payment/callback?reference=" + bookingReference;
  }


  private <T> T unwrapResponse(ApiResponse<T> response, String fallbackMessage)
  {
    if (response == null || !response.isSuccess() || response.getData() == null)
    {
      String message = response != null && response.getMessage() != null ? response.getMessage() : fallbackMessage;
      throw new TanteException(message);
    }
    return response.getData();
  }

  private boolean holdsTheSlot(ViewingBooking booking, LocalDateTime now)
  {
    boolean isHoldingSlot = false;
    if (booking.getStatus() == ViewingBookingStatus.PENDING || booking.getStatus() == ViewingBookingStatus.CONFIRMED)
    {
      isHoldingSlot = true;
    }
    else if (booking.getStatus() == ViewingBookingStatus.DRAFT)
    {
      isHoldingSlot = booking.getHoldExpiresAt() != null && booking.getHoldExpiresAt().isAfter(now);
    }

    return isHoldingSlot;
  }

  private ViewingBookingDetailsDTO toBookingDetails(ViewingBooking booking)
  {
    ViewingOutcomeDetailsDTO outcome = null;
    if (booking.getOutcome() != null)
    {
      outcome = new ViewingOutcomeDetailsDTO(
        booking.getOutcome().getId(),
        booking.getId(),
        booking.getOutcome().getType(),
        booking.getOutcome().getNotes(),
        booking.getOutcome().getRecordedAt()
      );
    }

    return new ViewingBookingDetailsDTO(
      booking.getId(),
      booking.getReference(),
      booking.getRentalProfile() != null ? booking.getRentalProfile().getId() : null,
      booking.getUnitId(),
      booking.getStartTime(),
      booking.getEndTime(),
      booking.getRequesterUserAccountId(),
      booking.getStatus(),
      booking.getServiceFee(),
      booking.getPaymentStatus(),
      booking.getPaymentUpdatedAt(),
      booking.getHoldExpiresAt(),
      booking.getPaymentUrl(),
      booking.getNotes(),
      outcome
    );
  }
}
