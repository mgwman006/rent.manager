package tz.tante.rent.manager.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tz.tante.rent.manager.models.dtos.ApiResponse;
import tz.tante.rent.manager.models.dtos.requests.landlord.LandLordRequestDto;
import tz.tante.rent.manager.models.dtos.responses.LandLordResponseDto;
import tz.tante.rent.manager.services.LandLordService;
import java.net.URI;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/auth")
public class AuthController
{
  private final LandLordService landLordService;

  @PostMapping("/landlord")
  public ResponseEntity<ApiResponse<LandLordResponseDto>> registerLandLord(
    @Valid @RequestBody LandLordRequestDto requestDto)
  {
    LandLordResponseDto createdLandLord = landLordService.registerLandLord(requestDto);
    URI location = URI.create("landlords/"+0);
    return ResponseEntity.created(location).
      body(ApiResponse.success(createdLandLord, HttpStatus.CREATED.value()));
  }
}
