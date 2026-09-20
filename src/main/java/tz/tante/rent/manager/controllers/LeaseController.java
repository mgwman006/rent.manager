package tz.tante.rent.manager.controllers;

import lombok.AllArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.tante.rent.manager.engines.rent.RentCalculator;
import tz.tante.rent.manager.engines.rent.dtos.RentCollectionSummary;
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
  private final RentCalculator rentCalculator;

  @PostMapping("/landlord")
  public ResponseEntity<ApiResponse<LeaseDetailsDTO>> createLeaseByLandlord(@Valid @RequestBody LeaseCreateDTO leaseCreateDTO)
  {
    LeaseDetailsDTO leaseDetails = leaseService.createLeaseInitiatedByLandlord(leaseCreateDTO);
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(ApiResponse.success(leaseDetails, HttpStatus.CREATED.value()));
  }

  @PostMapping("/tenant")
  public ResponseEntity<ApiResponse<LeaseDetailsDTO>> createLeaseByTenant(@Valid @RequestBody LeaseCreateDTO leaseCreateDTO)
  {
    LeaseDetailsDTO leaseDetails = leaseService.createLeaseInitiatedByTenant(leaseCreateDTO);
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(ApiResponse.success(leaseDetails, HttpStatus.CREATED.value()));
  }

  @GetMapping("/rental-profile/{rentalProfileId}")
  public ResponseEntity<ApiResponse<List<LeaseDetailsDTO>>> getLeasesByRentalProfile(@PathVariable Long rentalProfileId)
  {
    List<LeaseDetailsDTO> leases = leaseService.getLeasesByRentalProfile(rentalProfileId);
    return ResponseEntity.status(HttpStatus.OK)
      .body(ApiResponse.success(leases, HttpStatus.OK.value()));
  }

  @GetMapping("/{leaseId}")
  public ResponseEntity<ApiResponse<LeaseDetailsDTO>> getLeaseById(@PathVariable Long leaseId)
  {
    LeaseDetailsDTO leaseDetails = leaseService.getLeaseById(leaseId);
    return ResponseEntity.status(HttpStatus.OK)
      .body(ApiResponse.success(leaseDetails, HttpStatus.OK.value()));
  }

  @GetMapping("/tenant/{tenantId}")
  public ResponseEntity<ApiResponse<List<LeaseDetailsDTO>>> getActiveLeasesByTenant(@PathVariable Long tenantId)
  {
    List<LeaseDetailsDTO> leases = leaseService.getActiveLeasesByTenant(tenantId);
    return ResponseEntity.status(HttpStatus.OK)
      .body(ApiResponse.success(leases, HttpStatus.OK.value()));
  }

  @GetMapping("/rent/summary/{leaseId}")
  public ResponseEntity<ApiResponse<RentCollectionSummary>> getLeasePaymentStatus(@PathVariable Long leaseId)
  {
    RentCollectionSummary rentCollectionSummary = rentCalculator.getLeasePaymentStatus(leaseId);
    return ResponseEntity.status(HttpStatus.OK)
      .body(ApiResponse.success(rentCollectionSummary, HttpStatus.OK.value()));
  }
}
