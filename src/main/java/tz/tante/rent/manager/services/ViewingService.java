package tz.tante.rent.manager.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tz.tante.rent.manager.enums.ViewingOutcomeType;
import tz.tante.rent.manager.enums.ViewingSlotStatus;
import tz.tante.rent.manager.enums.ViewingStatus;
import tz.tante.rent.manager.exceptions.ResourceNotFoundException;
import tz.tante.rent.manager.exceptions.TanteException;
import tz.tante.rent.manager.models.dtos.requests.viewings.CreateViewingRequestDTO;
import tz.tante.rent.manager.models.dtos.requests.viewings.CreateViewingSlotDTO;
import tz.tante.rent.manager.models.dtos.requests.viewings.RecordViewingOutcomeDTO;
import tz.tante.rent.manager.models.dtos.responses.viewings.ViewingOutcomeDetailsDTO;
import tz.tante.rent.manager.models.dtos.responses.viewings.ViewingRequestDetailsDTO;
import tz.tante.rent.manager.models.dtos.responses.viewings.ViewingSlotDetailsDTO;
import tz.tante.rent.manager.models.entities.RentalProfile;
import tz.tante.rent.manager.models.entities.ViewingOutcome;
import tz.tante.rent.manager.models.entities.ViewingRequest;
import tz.tante.rent.manager.models.entities.ViewingSlot;
import tz.tante.rent.manager.repositories.RentalProfileRepository;
import tz.tante.rent.manager.repositories.ViewingOutcomeRepository;
import tz.tante.rent.manager.repositories.ViewingRequestRepository;
import tz.tante.rent.manager.repositories.ViewingSlotRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class ViewingService {
  private final ViewingSlotRepository viewingSlotRepository;
  private final ViewingRequestRepository viewingRequestRepository;
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

    if (viewingSlotRepository.findByRentalProfileId(rentalProfileId).stream()
      .filter(slot -> slot.getStatus() != ViewingSlotStatus.CANCELLED)
      .anyMatch(slot -> !request.endTime().isBefore(slot.getStartTime()) && !request.startTime().isAfter(slot.getEndTime()))) {
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
  public ViewingRequestDetailsDTO createViewingRequest(CreateViewingRequestDTO request) {
    if (request.requesterUserId() == null) {
      throw new TanteException("Requester user ID is required.");
    }

    ViewingSlot viewingSlot = viewingSlotRepository.findById(request.slotId())
      .orElseThrow(() -> new ResourceNotFoundException("Viewing slot with ID " + request.slotId() + " not found."));

    if (viewingSlot.getStatus() == ViewingSlotStatus.CANCELLED) {
      throw new TanteException("This viewing slot is cancelled and cannot accept new requests.");
    }

    boolean activeRequestExists = viewingRequestRepository.findBySlotId(viewingSlot.getId()).stream()
      .anyMatch(existingRequest -> existingRequest.getStatus() != ViewingStatus.REJECTED
        && existingRequest.getStatus() != ViewingStatus.CANCELLED
        && existingRequest.getStatus() != ViewingStatus.NO_SHOW
        && existingRequest.getStatus() != ViewingStatus.COMPLETED);

    if (activeRequestExists) {
      throw new TanteException("This viewing slot already has an active booking request.");
    }

    ViewingRequest viewingRequest = new ViewingRequest();
    viewingRequest.setSlot(viewingSlot);
    viewingRequest.setRequesterUserId(request.requesterUserId());
    viewingRequest.setServiceFee(viewingSlot.getServiceFee());
    viewingRequest.setNotes(request.notes());
    viewingRequest.setStatus(ViewingStatus.PENDING);
    viewingRequest.setFeePaid(false);

    viewingSlot.setStatus(ViewingSlotStatus.BOOKED);
    viewingRequest = viewingRequestRepository.save(viewingRequest);
    viewingSlotRepository.save(viewingSlot);

    return toRequestDetails(viewingRequest);
  }

  public List<ViewingRequestDetailsDTO> getRequestsByTenant(Long tenantUserId) {
    return viewingRequestRepository.findByRequesterUserId(tenantUserId).stream()
      .sorted(Comparator.comparing(ViewingRequest::getCreatedAt, Comparator.nullsLast(LocalDateTime::compareTo)).reversed())
      .map(this::toRequestDetails)
      .toList();
  }

  public List<ViewingRequestDetailsDTO> getRequestsByRentalProfile(Long rentalProfileId) {
    return viewingRequestRepository.findBySlotRentalProfileId(rentalProfileId).stream()
      .sorted(Comparator.comparing(ViewingRequest::getCreatedAt, Comparator.nullsLast(LocalDateTime::compareTo)).reversed())
      .map(this::toRequestDetails)
      .toList();
  }

  public ViewingRequestDetailsDTO getRequest(Long requestId) {
    ViewingRequest request = viewingRequestRepository.findById(requestId)
      .orElseThrow(() -> new ResourceNotFoundException("Viewing request with ID " + requestId + " not found."));
    return toRequestDetails(request);
  }

  @Transactional
  public ViewingRequestDetailsDTO approveRequest(Long requestId) {
    ViewingRequest request = viewingRequestRepository.findById(requestId)
      .orElseThrow(() -> new ResourceNotFoundException("Viewing request with ID " + requestId + " not found."));

    if (request.getStatus() != ViewingStatus.PENDING) {
      throw new TanteException("Only pending viewing requests can be approved.");
    }

    request.setStatus(ViewingStatus.APPROVED);
    request = viewingRequestRepository.save(request);
    return toRequestDetails(request);
  }

  @Transactional
  public ViewingRequestDetailsDTO rejectRequest(Long requestId) {
    ViewingRequest request = viewingRequestRepository.findById(requestId)
      .orElseThrow(() -> new ResourceNotFoundException("Viewing request with ID " + requestId + " not found."));

    if (request.getStatus() == ViewingStatus.CANCELLED || request.getStatus() == ViewingStatus.COMPLETED || request.getStatus() == ViewingStatus.NO_SHOW) {
      throw new TanteException("This viewing request cannot be rejected from its current status.");
    }

    request.setStatus(ViewingStatus.REJECTED);
    if (viewingRequestRepository.findBySlotId(request.getSlot().getId()).stream()
      .noneMatch(existingRequest -> existingRequest.getStatus() != ViewingStatus.REJECTED && existingRequest.getStatus() != ViewingStatus.CANCELLED && existingRequest.getStatus() != ViewingStatus.NO_SHOW && existingRequest.getStatus() != ViewingStatus.COMPLETED)) {
      request.getSlot().setStatus(ViewingSlotStatus.AVAILABLE);
      viewingSlotRepository.save(request.getSlot());
    }

    request = viewingRequestRepository.save(request);
    return toRequestDetails(request);
  }

  @Transactional
  public ViewingRequestDetailsDTO markFeePaid(Long requestId) {
    ViewingRequest request = viewingRequestRepository.findById(requestId)
      .orElseThrow(() -> new ResourceNotFoundException("Viewing request with ID " + requestId + " not found."));

    if (request.getStatus() != ViewingStatus.APPROVED && request.getStatus() != ViewingStatus.PENDING) {
      throw new TanteException("Only approved or pending viewing requests can be marked as paid.");
    }

    request.setFeePaid(true);
    request.setStatus(ViewingStatus.PAID);
    request = viewingRequestRepository.save(request);
    return toRequestDetails(request);
  }

  @Transactional
  public ViewingRequestDetailsDTO confirmBooking(Long requestId) {
    ViewingRequest request = viewingRequestRepository.findById(requestId)
      .orElseThrow(() -> new ResourceNotFoundException("Viewing request with ID " + requestId + " not found."));

    if (request.getServiceFee().compareTo(BigDecimal.ZERO) > 0 && !request.isFeePaid()) {
      throw new TanteException("The viewing fee must be paid before the booking can be confirmed.");
    }

    if (request.getStatus() != ViewingStatus.PAID && request.getStatus() != ViewingStatus.APPROVED) {
      throw new TanteException("Only approved or paid viewing requests can be confirmed.");
    }

    request.setStatus(ViewingStatus.CONFIRMED);
    request = viewingRequestRepository.save(request);
    return toRequestDetails(request);
  }

  @Transactional
  public ViewingRequestDetailsDTO recordOutcome(Long requestId, RecordViewingOutcomeDTO request) {
    ViewingRequest viewingRequest = viewingRequestRepository.findById(requestId)
      .orElseThrow(() -> new ResourceNotFoundException("Viewing request with ID " + requestId + " not found."));

    if (viewingRequest.getStatus() != ViewingStatus.CONFIRMED) {
      throw new TanteException("Only confirmed viewing requests can have an outcome recorded.");
    }

    ViewingOutcome viewingOutcome = new ViewingOutcome();
    viewingOutcome.setRequest(viewingRequest);
    viewingOutcome.setType(request.outcome());
    viewingOutcome.setNotes(request.notes());
    viewingOutcome.setRecordedAt(LocalDateTime.now());

    viewingOutcome = viewingOutcomeRepository.save(viewingOutcome);

    switch (request.outcome()) {
      case COMPLETED -> viewingRequest.setStatus(ViewingStatus.COMPLETED);
      case NO_SHOW -> viewingRequest.setStatus(ViewingStatus.NO_SHOW);
      case CANCELLED -> viewingRequest.setStatus(ViewingStatus.CANCELLED);
      case RESCHEDULED -> viewingRequest.setStatus(ViewingStatus.PENDING);
      default -> throw new TanteException("Unsupported viewing outcome type.");
    }

    viewingRequest.setOutcome(viewingOutcome);
    viewingRequest = viewingRequestRepository.save(viewingRequest);
    return toRequestDetails(viewingRequest);
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

  private ViewingRequestDetailsDTO toRequestDetails(ViewingRequest request) {
    ViewingOutcomeDetailsDTO outcome = null;
    if (request.getOutcome() != null) {
      outcome = new ViewingOutcomeDetailsDTO(
        request.getOutcome().getId(),
        request.getId(),
        request.getOutcome().getType(),
        request.getOutcome().getNotes(),
        request.getOutcome().getRecordedAt()
      );
    }

    return new ViewingRequestDetailsDTO(
      request.getId(),
      request.getSlot() != null ? request.getSlot().getId() : null,
      request.getRequesterUserId(),
      request.getStatus(),
      request.getServiceFee(),
      request.isFeePaid(),
      request.getNotes(),
      outcome
    );
  }
}
