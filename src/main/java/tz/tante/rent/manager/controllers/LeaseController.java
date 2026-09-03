package tz.tante.rent.manager.controllers;

import lombok.AllArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.tante.rent.manager.models.dtos.ApiResponse;
import tz.tante.rent.manager.models.dtos.requests.leases.LeaseCreateDTO;
import tz.tante.rent.manager.models.dtos.responses.LeaseDetailsDTO;
import tz.tante.rent.manager.services.LeaseService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/leases")
public class LeaseController
{
  private final LeaseService leaseService;

  @GetMapping("/rental-profile/{rentalProfileId}")
  public ResponseEntity<ApiResponse<List<LeaseDetailsDTO>>> getLeasesByRentalProfile(@PathVariable Long rentalProfileId)
  {
    List<LeaseDetailsDTO> leases = leaseService.getLeasesByRentalProfile(rentalProfileId);
    return ResponseEntity.status(HttpStatus.OK)
      .body(ApiResponse.success(leases, HttpStatus.OK.value()));
  }

  @PostMapping
  public ResponseEntity<ApiResponse<LeaseDetailsDTO>> createLease(@Valid @RequestBody LeaseCreateDTO leaseCreateDTO)
  {
    LeaseDetailsDTO leaseDetails = leaseService.createLease(leaseCreateDTO);
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(ApiResponse.success(leaseDetails, HttpStatus.CREATED.value()));
  }
}
