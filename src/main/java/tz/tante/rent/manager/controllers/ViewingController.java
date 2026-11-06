package tz.tante.rent.manager.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.tante.rent.manager.models.dtos.ApiResponse;
import tz.tante.rent.manager.models.dtos.requests.viewings.CreateViewingRequestDTO;
import tz.tante.rent.manager.models.dtos.requests.viewings.CreateViewingSlotDTO;
import tz.tante.rent.manager.models.dtos.requests.viewings.RecordViewingOutcomeDTO;
import tz.tante.rent.manager.models.dtos.responses.viewings.ViewingRequestDetailsDTO;
import tz.tante.rent.manager.models.dtos.responses.viewings.ViewingSlotDetailsDTO;
import tz.tante.rent.manager.services.ViewingService;

import java.util.List;

@RestController
@RequestMapping("/v1/viewings")
@AllArgsConstructor
public class ViewingController {
  private final ViewingService viewingService;

  @PostMapping("/slots/{rentalProfileId}")
  public ResponseEntity<ApiResponse<ViewingSlotDetailsDTO>> createViewingSlot(
    @PathVariable Long rentalProfileId,
    @Valid @RequestBody CreateViewingSlotDTO request
  ) {
    ViewingSlotDetailsDTO response = viewingService.createViewingSlot(rentalProfileId, request);
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(ApiResponse.success(response, HttpStatus.CREATED.value()));
  }

  @GetMapping("/slots/{rentalProfileId}")
  public ResponseEntity<ApiResponse<List<ViewingSlotDetailsDTO>>> getViewingSlotsByRentalProfile(@PathVariable Long rentalProfileId) {
    List<ViewingSlotDetailsDTO> response = viewingService.getSlotsByRentalProfile(rentalProfileId);
    return ResponseEntity.status(HttpStatus.OK)
      .body(ApiResponse.success(response, HttpStatus.OK.value()));
  }

  @GetMapping("/slots/{slotId}/details")
  public ResponseEntity<ApiResponse<ViewingSlotDetailsDTO>> getViewingSlot(@PathVariable Long slotId) {
    ViewingSlotDetailsDTO response = viewingService.getSlot(slotId);
    return ResponseEntity.status(HttpStatus.OK)
      .body(ApiResponse.success(response, HttpStatus.OK.value()));
  }

  @PatchMapping("/slots/{slotId}/cancel")
  public ResponseEntity<ApiResponse<ViewingSlotDetailsDTO>> cancelViewingSlot(@PathVariable Long slotId) {
    ViewingSlotDetailsDTO response = viewingService.cancelSlot(slotId);
    return ResponseEntity.status(HttpStatus.OK)
      .body(ApiResponse.success(response, HttpStatus.OK.value()));
  }

  @PostMapping("/requests")
  public ResponseEntity<ApiResponse<ViewingRequestDetailsDTO>> createViewingRequest(@Valid @RequestBody CreateViewingRequestDTO request) {
    ViewingRequestDetailsDTO response = viewingService.createViewingRequest(request);
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(ApiResponse.success(response, HttpStatus.CREATED.value()));
  }

  @GetMapping("/requests/{requestId}")
  public ResponseEntity<ApiResponse<ViewingRequestDetailsDTO>> getViewingRequest(@PathVariable Long requestId) {
    ViewingRequestDetailsDTO response = viewingService.getRequest(requestId);
    return ResponseEntity.status(HttpStatus.OK)
      .body(ApiResponse.success(response, HttpStatus.OK.value()));
  }

  @GetMapping("/requests/by-tenant/{tenantUserId}")
  public ResponseEntity<ApiResponse<List<ViewingRequestDetailsDTO>>> getViewingRequestsByTenant(@PathVariable Long tenantUserId) {
    List<ViewingRequestDetailsDTO> response = viewingService.getRequestsByTenant(tenantUserId);
    return ResponseEntity.status(HttpStatus.OK)
      .body(ApiResponse.success(response, HttpStatus.OK.value()));
  }

  @GetMapping("/requests/by-rental-profile/{rentalProfileId}")
  public ResponseEntity<ApiResponse<List<ViewingRequestDetailsDTO>>> getViewingRequestsByRentalProfile(@PathVariable Long rentalProfileId) {
    List<ViewingRequestDetailsDTO> response = viewingService.getRequestsByRentalProfile(rentalProfileId);
    return ResponseEntity.status(HttpStatus.OK)
      .body(ApiResponse.success(response, HttpStatus.OK.value()));
  }

  @PatchMapping("/requests/{requestId}/approve")
  public ResponseEntity<ApiResponse<ViewingRequestDetailsDTO>> approveViewingRequest(@PathVariable Long requestId) {
    ViewingRequestDetailsDTO response = viewingService.approveRequest(requestId);
    return ResponseEntity.status(HttpStatus.OK)
      .body(ApiResponse.success(response, HttpStatus.OK.value()));
  }

  @PatchMapping("/requests/{requestId}/reject")
  public ResponseEntity<ApiResponse<ViewingRequestDetailsDTO>> rejectViewingRequest(@PathVariable Long requestId) {
    ViewingRequestDetailsDTO response = viewingService.rejectRequest(requestId);
    return ResponseEntity.status(HttpStatus.OK)
      .body(ApiResponse.success(response, HttpStatus.OK.value()));
  }

  @PatchMapping("/requests/{requestId}/pay")
  public ResponseEntity<ApiResponse<ViewingRequestDetailsDTO>> markViewingFeePaid(@PathVariable Long requestId) {
    ViewingRequestDetailsDTO response = viewingService.markFeePaid(requestId);
    return ResponseEntity.status(HttpStatus.OK)
      .body(ApiResponse.success(response, HttpStatus.OK.value()));
  }

  @PatchMapping("/requests/{requestId}/confirm")
  public ResponseEntity<ApiResponse<ViewingRequestDetailsDTO>> confirmViewingBooking(@PathVariable Long requestId) {
    ViewingRequestDetailsDTO response = viewingService.confirmBooking(requestId);
    return ResponseEntity.status(HttpStatus.OK)
      .body(ApiResponse.success(response, HttpStatus.OK.value()));
  }

  @PatchMapping("/requests/{requestId}/outcome")
  public ResponseEntity<ApiResponse<ViewingRequestDetailsDTO>> recordViewingOutcome(
    @PathVariable Long requestId,
    @Valid @RequestBody RecordViewingOutcomeDTO request
  ) {
    ViewingRequestDetailsDTO response = viewingService.recordOutcome(requestId, request);
    return ResponseEntity.status(HttpStatus.OK)
      .body(ApiResponse.success(response, HttpStatus.OK.value()));
  }
}
