package tz.tante.rent.manager.controllers;

import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tz.tante.rent.manager.models.dtos.ApiResponse;
import tz.tante.rent.manager.models.dtos.responses.TenantDetailsDTO;
import tz.tante.rent.manager.services.TenantService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/tenants")
@Setter
@AllArgsConstructor
public class TenantController
{
  private final TenantService tenantService;

  @GetMapping
  public ResponseEntity<ApiResponse<TenantDetailsDTO>> getTenantDetailsByUserId(@RequestParam Long userId)
  {
      TenantDetailsDTO tenantDetails = tenantService.getTenantDetailsByUserId(userId);
      return ResponseEntity.status(HttpStatus.OK).
        body(ApiResponse.success(tenantDetails, HttpStatus.OK.value()));
  }

}
