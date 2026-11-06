package tz.tante.rent.manager.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tz.tante.rent.manager.enums.ViewingBookingStatus;
import tz.tante.rent.manager.exceptions.ResourceNotFoundException;
import tz.tante.rent.manager.exceptions.TanteException;
import tz.tante.rent.manager.models.dtos.requests.viewings.CreateViewingBookingDTO;
import tz.tante.rent.manager.models.dtos.requests.viewings.RecordViewingOutcomeDTO;
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
import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class ViewingService {
  private final ViewingBookingRepository viewingBookingRepository;
  private final ViewingOutcomeRepository viewingOutcomeRepository;
  private final RentalProfileRepository rentalProfileRepository;

  @Transactional
  public ViewingBookingDetailsDTO createViewingBooking(CreateViewingBookingDTO request) {
    if (request.startTime().isAfter(request.endTime()) || request.startTime().isEqual(request.endTime())) {
      throw new TanteException("Viewing start time must be before end time.");
    }

    if (request.serviceFee().compareTo(BigDecimal.ZERO) < 0) {
      throw new TanteException("Viewing fee cannot be negative.");
    }

    RentalProfile rentalProfile = rentalProfileRepository.findById(request.rentalProfileId())
      .orElseThrow(() -> new ResourceNotFoundException("Rental profile with ID " + request.rentalProfileId() + " not found."));

    if (hasActiveBookingConflict(rentalProfile.getId(), request.startTime(), request.endTime())) {
      throw new TanteException("No agent is available for the requested time because of an overlapping active viewing.");
    }

    ViewingBooking booking = new ViewingBooking();
    booking.setRentalProfile(rentalProfile);
    booking.setPropertyId(request.propertyId());
    booking.setStartTime(request.startTime());
    booking.setEndTime(request.endTime());
    booking.setRequesterUserId(request.requesterUserId());
    booking.setServiceFee(request.serviceFee());
    booking.setNotes(request.notes());
    booking.setStatus(ViewingBookingStatus.PENDING);
    booking.setFeePaid(false);

    booking = viewingBookingRepository.save(booking);
    return toBookingDetails(booking);
  }

  public List<ViewingBookingDetailsDTO> getBookingsByRequester(Long requesterUserId) {
    return viewingBookingRepository.findByRequesterUserId(requesterUserId).stream()
      .sorted(Comparator.comparing(ViewingBooking::getStartTime).reversed())
      .map(this::toBookingDetails)
      .toList();
  }

  public List<ViewingBookingDetailsDTO> getBookingsByRentalProfile(Long rentalProfileId) {
    return viewingBookingRepository.findByRentalProfileId(rentalProfileId).stream()
      .sorted(Comparator.comparing(ViewingBooking::getStartTime))
      .map(this::toBookingDetails)
      .toList();
  }

  public ViewingBookingDetailsDTO getBooking(Long bookingId) {
    return toBookingDetails(findBooking(bookingId));
  }

  @Transactional
  public ViewingBookingDetailsDTO approveBooking(Long bookingId) {
    ViewingBooking booking = findBooking(bookingId);

    if (booking.getStatus() != ViewingBookingStatus.PENDING) {
      throw new TanteException("Only pending viewing bookings can be approved.");
    }

    booking.setStatus(ViewingBookingStatus.APPROVED);
    return toBookingDetails(viewingBookingRepository.save(booking));
  }

  @Transactional
  public ViewingBookingDetailsDTO rejectBooking(Long bookingId) {
    ViewingBooking booking = findBooking(bookingId);

    if (booking.getStatus() == ViewingBookingStatus.CANCELLED || booking.getStatus() == ViewingBookingStatus.COMPLETED || booking.getStatus() == ViewingBookingStatus.NO_SHOW) {
      throw new TanteException("This viewing booking cannot be rejected from its current status.");
    }

    booking.setStatus(ViewingBookingStatus.REJECTED);
    return toBookingDetails(viewingBookingRepository.save(booking));
  }

  @Transactional
  public ViewingBookingDetailsDTO markFeePaid(Long bookingId) {
    ViewingBooking booking = findBooking(bookingId);

    if (booking.getStatus() != ViewingBookingStatus.APPROVED && booking.getStatus() != ViewingBookingStatus.PENDING) {
      throw new TanteException("Only approved or pending viewing bookings can be marked as paid.");
    }

    booking.setFeePaid(true);
    booking.setStatus(ViewingBookingStatus.PAID);
    return toBookingDetails(viewingBookingRepository.save(booking));
  }

  @Transactional
  public ViewingBookingDetailsDTO confirmBooking(Long bookingId) {
    ViewingBooking booking = findBooking(bookingId);

    if (booking.getServiceFee().compareTo(BigDecimal.ZERO) > 0 && !booking.isFeePaid()) {
      throw new TanteException("The viewing fee must be paid before the booking can be confirmed.");
    }

    if (booking.getStatus() != ViewingBookingStatus.PAID && booking.getStatus() != ViewingBookingStatus.APPROVED) {
      throw new TanteException("Only approved or paid viewing bookings can be confirmed.");
    }

    booking.setStatus(ViewingBookingStatus.CONFIRMED);
    return toBookingDetails(viewingBookingRepository.save(booking));
  }

  @Transactional
  public ViewingBookingDetailsDTO recordOutcome(Long bookingId, RecordViewingOutcomeDTO request) {
    ViewingBooking booking = findBooking(bookingId);

    if (booking.getStatus() != ViewingBookingStatus.CONFIRMED) {
      throw new TanteException("Only confirmed viewing bookings can have an outcome recorded.");
    }

    ViewingOutcome viewingOutcome = new ViewingOutcome();
    viewingOutcome.setBooking(booking);
    viewingOutcome.setType(request.outcome());
    viewingOutcome.setNotes(request.notes());
    viewingOutcome.setRecordedAt(LocalDateTime.now());
    viewingOutcome = viewingOutcomeRepository.save(viewingOutcome);

    switch (request.outcome()) {
      case COMPLETED -> booking.setStatus(ViewingBookingStatus.COMPLETED);
      case NO_SHOW -> booking.setStatus(ViewingBookingStatus.NO_SHOW);
      case CANCELLED, RESCHEDULED -> booking.setStatus(ViewingBookingStatus.CANCELLED);
    }

    booking.setOutcome(viewingOutcome);
    return toBookingDetails(viewingBookingRepository.save(booking));
  }

  private ViewingBooking findBooking(Long bookingId) {
    return viewingBookingRepository.findById(bookingId)
      .orElseThrow(() -> new ResourceNotFoundException("Viewing booking with ID " + bookingId + " not found."));
  }

  private boolean hasActiveBookingConflict(Long rentalProfileId, LocalDateTime startTime, LocalDateTime endTime) {
    return viewingBookingRepository.findByRentalProfileId(rentalProfileId).stream()
      .filter(booking -> booking.getStatus() != ViewingBookingStatus.REJECTED
        && booking.getStatus() != ViewingBookingStatus.CANCELLED
        && booking.getStatus() != ViewingBookingStatus.NO_SHOW
        && booking.getStatus() != ViewingBookingStatus.COMPLETED)
      .anyMatch(booking -> startTime.isBefore(booking.getEndTime()) && endTime.isAfter(booking.getStartTime()));
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
      booking.getRentalProfile() != null ? booking.getRentalProfile().getId() : null,
      booking.getPropertyId(),
      booking.getStartTime(),
      booking.getEndTime(),
      booking.getRequesterUserId(),
      booking.getStatus(),
      booking.getServiceFee(),
      booking.isFeePaid(),
      booking.getNotes(),
      outcome
    );
  }
}
