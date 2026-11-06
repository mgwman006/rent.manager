package tz.tante.rent.manager.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.tante.rent.manager.models.dtos.ApiResponse;
import tz.tante.rent.manager.models.dtos.requests.viewings.CreateViewingBookingDTO;
import tz.tante.rent.manager.models.dtos.requests.viewings.RecordViewingOutcomeDTO;
import tz.tante.rent.manager.models.dtos.responses.viewings.ViewingBookingDetailsDTO;
import tz.tante.rent.manager.services.ViewingService;

import java.util.List;

@RestController
@RequestMapping("/v1/viewings")
@AllArgsConstructor
public class ViewingController {
  private final ViewingService viewingService;

  @PostMapping("/bookings")
  public ResponseEntity<ApiResponse<ViewingBookingDetailsDTO>> createViewingBooking(@Valid @RequestBody CreateViewingBookingDTO request) {
    ViewingBookingDetailsDTO response = viewingService.createViewingBooking(request);
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(ApiResponse.success(response, HttpStatus.CREATED.value()));
  }

  @GetMapping("/bookings/{bookingId}")
  public ResponseEntity<ApiResponse<ViewingBookingDetailsDTO>> getViewingBooking(@PathVariable Long bookingId) {
    ViewingBookingDetailsDTO response = viewingService.getBooking(bookingId);
    return ResponseEntity.status(HttpStatus.OK)
      .body(ApiResponse.success(response, HttpStatus.OK.value()));
  }

  @GetMapping("/bookings/by-requester/{requesterUserId}")
  public ResponseEntity<ApiResponse<List<ViewingBookingDetailsDTO>>> getViewingBookingsByRequester(@PathVariable Long requesterUserId) {
    List<ViewingBookingDetailsDTO> response = viewingService.getBookingsByRequester(requesterUserId);
    return ResponseEntity.status(HttpStatus.OK)
      .body(ApiResponse.success(response, HttpStatus.OK.value()));
  }

  @GetMapping("/bookings/by-rental-profile/{rentalProfileId}")
  public ResponseEntity<ApiResponse<List<ViewingBookingDetailsDTO>>> getViewingBookingsByRentalProfile(@PathVariable Long rentalProfileId) {
    List<ViewingBookingDetailsDTO> response = viewingService.getBookingsByRentalProfile(rentalProfileId);
    return ResponseEntity.status(HttpStatus.OK)
      .body(ApiResponse.success(response, HttpStatus.OK.value()));
  }

  @PatchMapping("/bookings/{bookingId}/approve")
  public ResponseEntity<ApiResponse<ViewingBookingDetailsDTO>> approveViewingBooking(@PathVariable Long bookingId) {
    ViewingBookingDetailsDTO response = viewingService.approveBooking(bookingId);
    return ResponseEntity.status(HttpStatus.OK)
      .body(ApiResponse.success(response, HttpStatus.OK.value()));
  }

  @PatchMapping("/bookings/{bookingId}/reject")
  public ResponseEntity<ApiResponse<ViewingBookingDetailsDTO>> rejectViewingBooking(@PathVariable Long bookingId) {
    ViewingBookingDetailsDTO response = viewingService.rejectBooking(bookingId);
    return ResponseEntity.status(HttpStatus.OK)
      .body(ApiResponse.success(response, HttpStatus.OK.value()));
  }

  @PatchMapping("/bookings/{bookingId}/pay")
  public ResponseEntity<ApiResponse<ViewingBookingDetailsDTO>> markViewingFeePaid(@PathVariable Long bookingId) {
    ViewingBookingDetailsDTO response = viewingService.markFeePaid(bookingId);
    return ResponseEntity.status(HttpStatus.OK)
      .body(ApiResponse.success(response, HttpStatus.OK.value()));
  }

  @PatchMapping("/bookings/{bookingId}/confirm")
  public ResponseEntity<ApiResponse<ViewingBookingDetailsDTO>> confirmViewingBooking(@PathVariable Long bookingId) {
    ViewingBookingDetailsDTO response = viewingService.confirmBooking(bookingId);
    return ResponseEntity.status(HttpStatus.OK)
      .body(ApiResponse.success(response, HttpStatus.OK.value()));
  }

  @PatchMapping("/bookings/{bookingId}/outcome")
  public ResponseEntity<ApiResponse<ViewingBookingDetailsDTO>> recordViewingOutcome(
    @PathVariable Long bookingId,
    @Valid @RequestBody RecordViewingOutcomeDTO request
  ) {
    ViewingBookingDetailsDTO response = viewingService.recordOutcome(bookingId, request);
    return ResponseEntity.status(HttpStatus.OK)
      .body(ApiResponse.success(response, HttpStatus.OK.value()));
  }
}
