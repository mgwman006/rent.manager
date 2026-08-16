package tz.tante.rent.manager.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tz.tante.rent.manager.models.dtos.ApiResponse;
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
}
