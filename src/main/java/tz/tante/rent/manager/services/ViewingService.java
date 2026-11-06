package tz.tante.rent.manager.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tz.tante.rent.manager.enums.ViewingOutcomeType;
import tz.tante.rent.manager.enums.ViewingSlotStatus;
import tz.tante.rent.manager.enums.ViewingStatus;
import tz.tante.rent.manager.exceptions.ResourceNotFoundException;
import tz.tante.rent.manager.exceptions.TanteException;
import tz.tante.rent.manager.models.dtos.requests.viewings.CreateViewingBookingDTO;
import tz.tante.rent.manager.models.dtos.requests.viewings.CreateViewingSlotDTO;
import tz.tante.rent.manager.models.dtos.requests.viewings.RecordViewingOutcomeDTO;
import tz.tante.rent.manager.models.dtos.responses.viewings.ViewingOutcomeDetailsDTO;
import tz.tante.rent.manager.models.dtos.responses.viewings.ViewingBookingDetailsDTO;
import tz.tante.rent.manager.models.dtos.responses.viewings.ViewingSlotDetailsDTO;
import tz.tante.rent.manager.models.entities.RentalProfile;
import tz.tante.rent.manager.models.entities.ViewingBooking;
import tz.tante.rent.manager.models.entities.ViewingOutcome;
import tz.tante.rent.manager.models.entities.ViewingSlot;
import tz.tante.rent.manager.repositories.RentalProfileRepository;
import tz.tante.rent.manager.repositories.ViewingBookingRepository;
import tz.tante.rent.manager.repositories.ViewingOutcomeRepository;
import tz.tante.rent.manager.repositories.ViewingSlotRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class ViewingService {
  private final ViewingSlotRepository viewingSlotRepository;
  private final ViewingBookingRepository viewingBookingRepository;
  private final ViewingOutcomeRepository viewingOutcomeRepository;
  private final RentalProfileRepository rentalProfileRepository;

  @Transactional
  public ViewingSlotDetailsDTO createViewingSlot(Long rentalProfileId, CreateViewingSlotDTO request) {
    RentalProfile rentalProfile = rentalProfileRepository.findById(rentalProfileId)
      .orElseThrow(() -> new ResourceNotFoundException("Rental profile with ID " + rentalProfileId + " not found."));

    if (request.startTime().isAfter(request.endTime()) || request.startTime().isEqual(request.endTime())) {
      throw new TanteException("Viewing start time must be before end time.");
    }

    if (request.serviceFee() == null || request.serviceFee().compareTo(BigDecimal.ZERO) < 0) {
      throw new TanteException("Viewing fee cannot be negative.");
    }

    boolean overlapsExistingSlot = viewingSlotRepository.findByRentalProfileId(rentalProfileId).stream()
      .filter(slot -> slot.getStatus() != ViewingSlotStatus.CANCELLED)
      .anyMatch(slot -> !request.endTime().isBefore(slot.getStartTime()) && !request.startTime().isAfter(slot.getEndTime()));

    if (overlapsExistingSlot) {
      throw new TanteException("This viewing slot overlaps with an existing slot for this agency profile.");
    }

    ViewingSlot viewingSlot = new ViewingSlot();
    viewingSlot.setRentalProfile(rentalProfile);
    viewingSlot.setPropertyId(request.propertyId());
    viewingSlot.setStartTime(request.startTime());
    viewingSlot.setEndTime(request.endTime());
    viewingSlot.setServiceFee(request.serviceFee());
    viewingSlot.setNotes(request.notes());
    viewingSlot.setStatus(ViewingSlotStatus.AVAILABLE);

    viewingSlot = viewingSlotRepository.save(viewingSlot);
    return toSlotDetails(viewingSlot);
  }

  public List<ViewingSlotDetailsDTO> getSlotsByRentalProfile(Long rentalProfileId) {
    return viewingSlotRepository.findByRentalProfileId(rentalProfileId).stream()
      .sorted(Comparator.comparing(ViewingSlot::getStartTime))
      .map(this::toSlotDetails)
      .toList();
  }

  public ViewingSlotDetailsDTO getSlot(Long slotId) {
    ViewingSlot viewingSlot = viewingSlotRepository.findById(slotId)
      .orElseThrow(() -> new ResourceNotFoundException("Viewing slot with ID " + slotId + " not found."));
    return toSlotDetails(viewingSlot);
  }

  @Transactional
  public ViewingSlotDetailsDTO cancelSlot(Long slotId) {
    ViewingSlot viewingSlot = viewingSlotRepository.findById(slotId)
      .orElseThrow(() -> new ResourceNotFoundException("Viewing slot with ID " + slotId + " not found."));

    if (viewingSlot.getStatus() == ViewingSlotStatus.BOOKED) {
      throw new TanteException("Only unbooked viewing slots can be cancelled.");
    }

    viewingSlot.setStatus(ViewingSlotStatus.CANCELLED);
    viewingSlot = viewingSlotRepository.save(viewingSlot);
    return toSlotDetails(viewingSlot);
  }

  @Transactional
  public ViewingBookingDetailsDTO createViewingBooking(CreateViewingBookingDTO request) {
    if (request.requesterUserId() == null) {
      throw new TanteException("Requester user ID is required.");
    }

    ViewingSlot viewingSlot = viewingSlotRepository.findById(request.slotId())
      .orElseThrow(() -> new ResourceNotFoundException("Viewing slot with ID " + request.slotId() + " not found."));

    if (viewingSlot.getStatus() == ViewingSlotStatus.CANCELLED) {
      throw new TanteException("This viewing slot is cancelled and cannot accept new bookings.");
    }

    boolean activeBookingExists = viewingBookingRepository.findBySlotId(viewingSlot.getId()).stream()
      .anyMatch(existingBooking -> existingBooking.getStatus() != ViewingStatus.REJECTED
        && existingBooking.getStatus() != ViewingStatus.CANCELLED
        && existingBooking.getStatus() != ViewingStatus.NO_SHOW
        && existingBooking.getStatus() != ViewingStatus.COMPLETED);

    if (activeBookingExists) {
      throw new TanteException("This viewing slot already has an active booking.");
    }

    ViewingBooking booking = new ViewingBooking();
    booking.setSlot(viewingSlot);
    booking.setRequesterUserId(request.requesterUserId());
    booking.setServiceFee(viewingSlot.getServiceFee());
    booking.setNotes(request.notes());
    booking.setStatus(ViewingStatus.PENDING);
    booking.setFeePaid(false);

    viewingSlot.setStatus(ViewingSlotStatus.BOOKED);
    booking = viewingBookingRepository.save(booking);
    viewingSlotRepository.save(viewingSlot);

    return toBookingDetails(booking);
  }

  public List<ViewingBookingDetailsDTO> getBookingsByRequester(Long requesterUserId) {
    return viewingBookingRepository.findByRequesterUserId(requesterUserId).stream()
      .sorted(Comparator.comparing(ViewingBooking::getCreatedAt, Comparator.nullsLast(LocalDateTime::compareTo)).reversed())
      .map(this::toBookingDetails)
      .toList();
  }

  public List<ViewingBookingDetailsDTO> getBookingsByRentalProfile(Long rentalProfileId) {
    return viewingBookingRepository.findBySlotRentalProfileId(rentalProfileId).stream()
      .sorted(Comparator.comparing(ViewingBooking::getCreatedAt, Comparator.nullsLast(LocalDateTime::compareTo)).reversed())
      .map(this::toBookingDetails)
      .toList();
  }

  public ViewingBookingDetailsDTO getBooking(Long bookingId) {
    ViewingBooking booking = viewingBookingRepository.findById(bookingId)
      .orElseThrow(() -> new ResourceNotFoundException("Viewing booking with ID " + bookingId + " not found."));
    return toBookingDetails(booking);
  }

  @Transactional
  public ViewingBookingDetailsDTO approveBooking(Long bookingId) {
    ViewingBooking booking = viewingBookingRepository.findById(bookingId)
      .orElseThrow(() -> new ResourceNotFoundException("Viewing booking with ID " + bookingId + " not found."));

    if (booking.getStatus() != ViewingStatus.PENDING) {
      throw new TanteException("Only pending viewing bookings can be approved.");
    }

    booking.setStatus(ViewingStatus.APPROVED);
    booking = viewingBookingRepository.save(booking);
    return toBookingDetails(booking);
  }

  @Transactional
  public ViewingBookingDetailsDTO rejectBooking(Long bookingId) {
    ViewingBooking booking = viewingBookingRepository.findById(bookingId)
      .orElseThrow(() -> new ResourceNotFoundException("Viewing booking with ID " + bookingId + " not found."));

    if (booking.getStatus() == ViewingStatus.CANCELLED || booking.getStatus() == ViewingStatus.COMPLETED || booking.getStatus() == ViewingStatus.NO_SHOW) {
      throw new TanteException("This viewing booking cannot be rejected from its current status.");
    }

    booking.setStatus(ViewingStatus.REJECTED);
    if (viewingBookingRepository.findBySlotId(booking.getSlot().getId()).stream()
      .noneMatch(existingBooking -> existingBooking.getStatus() != ViewingStatus.REJECTED
        && existingBooking.getStatus() != ViewingStatus.CANCELLED
        && existingBooking.getStatus() != ViewingStatus.NO_SHOW
        && existingBooking.getStatus() != ViewingStatus.COMPLETED)) {
      booking.getSlot().setStatus(ViewingSlotStatus.AVAILABLE);
      viewingSlotRepository.save(booking.getSlot());
    }

    booking = viewingBookingRepository.save(booking);
    return toBookingDetails(booking);
  }

  @Transactional
  public ViewingBookingDetailsDTO markFeePaid(Long bookingId) {
    ViewingBooking booking = viewingBookingRepository.findById(bookingId)
      .orElseThrow(() -> new ResourceNotFoundException("Viewing booking with ID " + bookingId + " not found."));

    if (booking.getStatus() != ViewingStatus.APPROVED && booking.getStatus() != ViewingStatus.PENDING) {
      throw new TanteException("Only approved or pending viewing bookings can be marked as paid.");
    }

    booking.setFeePaid(true);
    booking.setStatus(ViewingStatus.PAID);
    booking = viewingBookingRepository.save(booking);
    return toBookingDetails(booking);
  }

  @Transactional
  public ViewingBookingDetailsDTO confirmBooking(Long bookingId) {
    ViewingBooking booking = viewingBookingRepository.findById(bookingId)
      .orElseThrow(() -> new ResourceNotFoundException("Viewing booking with ID " + bookingId + " not found."));

    if (booking.getServiceFee().compareTo(BigDecimal.ZERO) > 0 && !booking.isFeePaid()) {
      throw new TanteException("The viewing fee must be paid before the booking can be confirmed.");
    }

    if (booking.getStatus() != ViewingStatus.PAID && booking.getStatus() != ViewingStatus.APPROVED) {
      throw new TanteException("Only approved or paid viewing bookings can be confirmed.");
    }

    booking.setStatus(ViewingStatus.CONFIRMED);
    booking = viewingBookingRepository.save(booking);
    return toBookingDetails(booking);
  }

  @Transactional
  public ViewingBookingDetailsDTO recordOutcome(Long bookingId, RecordViewingOutcomeDTO request) {
    ViewingBooking booking = viewingBookingRepository.findById(bookingId)
      .orElseThrow(() -> new ResourceNotFoundException("Viewing booking with ID " + bookingId + " not found."));

    if (booking.getStatus() != ViewingStatus.CONFIRMED) {
      throw new TanteException("Only confirmed viewing bookings can have an outcome recorded.");
    }

    ViewingOutcome viewingOutcome = new ViewingOutcome();
    viewingOutcome.setBooking(booking);
    viewingOutcome.setType(request.outcome());
    viewingOutcome.setNotes(request.notes());
    viewingOutcome.setRecordedAt(LocalDateTime.now());

    viewingOutcome = viewingOutcomeRepository.save(viewingOutcome);

    switch (request.outcome()) {
      case COMPLETED -> booking.setStatus(ViewingStatus.COMPLETED);
      case NO_SHOW -> booking.setStatus(ViewingStatus.NO_SHOW);
      case CANCELLED -> booking.setStatus(ViewingStatus.CANCELLED);
      case RESCHEDULED -> booking.setStatus(ViewingStatus.PENDING);
      default -> throw new TanteException("Unsupported viewing outcome type.");
    }

    booking.setOutcome(viewingOutcome);
    booking = viewingBookingRepository.save(booking);
    return toBookingDetails(booking);
  }

  private ViewingSlotDetailsDTO toSlotDetails(ViewingSlot viewingSlot) {
    return new ViewingSlotDetailsDTO(
      viewingSlot.getId(),
      viewingSlot.getRentalProfile() != null ? viewingSlot.getRentalProfile().getId() : null,
      viewingSlot.getPropertyId(),
      viewingSlot.getStartTime(),
      viewingSlot.getEndTime(),
      viewingSlot.getServiceFee(),
      viewingSlot.getStatus(),
      viewingSlot.getNotes()
    );
  }

  private ViewingBookingDetailsDTO toBookingDetails(ViewingBooking booking) {
    ViewingOutcomeDetailsDTO outcome = null;
    if (booking.getOutcome() != null) {
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
      booking.getSlot() != null ? booking.getSlot().getId() : null,
      booking.getRequesterUserId(),
      booking.getStatus(),
      booking.getServiceFee(),
      booking.isFeePaid(),
      booking.getNotes(),
      outcome
    );
  }
}
