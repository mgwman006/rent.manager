package tz.tante.rent.manager.controllers;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
  public ResponseEntity<ApiResponse<RentalProfileDetailsDTO>> createRentalProfile(@RequestBody CreateRentalProfileDTO request)
  {
    RentalProfileDetailsDTO rentalProfileDetailsDTO = rentalProfileService.createRentalProfile(request);
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(ApiResponse.success(rentalProfileDetailsDTO, HttpStatus.CREATED.value()));
  }
}
