package tz.tante.rent.manager.controllers;


import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.tante.rent.manager.models.dtos.ApiResponse;
import tz.tante.rent.manager.models.dtos.requests.rentalprofiles.CreateRentalProfileDTO;
import tz.tante.rent.manager.models.dtos.responses.rentalprofiles.RentalProfileDetailsDTO;
import tz.tante.rent.manager.services.RentalProfileService;

@RestController
@AllArgsConstructor
@Setter
@Getter
@RequestMapping("v1/rentalprofiles")
public class RentalProfileController
{
  private final RentalProfileService rentalProfileService;

  @PostMapping
  public ResponseEntity<ApiResponse<RentalProfileDetailsDTO>> createRentalProfile(@Valid @RequestBody CreateRentalProfileDTO request)
  {
    RentalProfileDetailsDTO rentalProfileDetailsDTO = rentalProfileService.createRentalProfile(request);
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(ApiResponse.success(rentalProfileDetailsDTO, HttpStatus.CREATED.value()));
  }

  @GetMapping("/{rentalProfileId}")
  public ResponseEntity<ApiResponse<RentalProfileDetailsDTO>> getRentalProfile(@PathVariable Long rentalProfileId)
  {
    RentalProfileDetailsDTO rentalProfileDetailsDTO = rentalProfileService.getRentalProfile(rentalProfileId);
    return ResponseEntity.status(HttpStatus.OK)
      .body(ApiResponse.success(rentalProfileDetailsDTO, HttpStatus.OK.value()));
  }
}
