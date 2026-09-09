package tz.tante.rent.manager.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.tante.rent.manager.models.dtos.ApiResponse;
import tz.tante.rent.manager.models.dtos.requests.tenantinvitation.TenantInvitationCreateDTO;
import tz.tante.rent.manager.models.dtos.responses.LeaseDetailsDTO;
import tz.tante.rent.manager.models.dtos.responses.TenantInvitationDetailsDTO;
import tz.tante.rent.manager.services.TenantInvitationService;

import java.util.UUID;

@RestController
@RequestMapping("/v1/tenant-invitations")
@AllArgsConstructor
@Setter
public class TenantInvitationController
{
    private final TenantInvitationService tenantInvitationService;

    @GetMapping("/{invitationToken}")
    public ResponseEntity<ApiResponse<TenantInvitationDetailsDTO>> getTenantInvitationDetails(@PathVariable UUID invitationToken)
    {
        TenantInvitationDetailsDTO tenantInvitationDetailsDTO = tenantInvitationService.getTenantInvitationDetails(invitationToken);
        return ResponseEntity.status(200)
          .body(ApiResponse.success(tenantInvitationDetailsDTO, 200));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TenantInvitationDetailsDTO>> createTenantInvitation(@Valid @RequestBody TenantInvitationCreateDTO tenantInvitationCreateDTO)
    {
        TenantInvitationDetailsDTO tenantInvitationDetailsDTO = tenantInvitationService.createTenantInvitation(tenantInvitationCreateDTO);
        return ResponseEntity.status(201)
          .body(ApiResponse.success(tenantInvitationDetailsDTO, 201));
    }

    @PostMapping("/{invitationToken}/accept")
    public ResponseEntity<ApiResponse<Void>> acceptTenantInvitation(@PathVariable UUID invitationToken, @RequestParam Long userId)
    {
        tenantInvitationService.acceptTenantInvitation(invitationToken, userId);
        return ResponseEntity.status(200)
                .body(ApiResponse.success(null, 200));
    }

}
