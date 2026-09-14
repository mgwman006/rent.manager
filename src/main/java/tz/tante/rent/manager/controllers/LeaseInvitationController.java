package tz.tante.rent.manager.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.tante.rent.manager.models.dtos.ApiResponse;
import tz.tante.rent.manager.models.dtos.requests.leaseinvitation.LeaseInvitationCreateDTO;
import tz.tante.rent.manager.models.dtos.responses.LeaseInvitationDetailsDTO;
import tz.tante.rent.manager.services.LeaseInvitationService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/lease-invitations")
@AllArgsConstructor
@Setter
public class LeaseInvitationController
{
    private final LeaseInvitationService leaseInvitationService;

    @GetMapping("/{invitationToken}")
    public ResponseEntity<ApiResponse<LeaseInvitationDetailsDTO>> getInvitationDetails(@PathVariable UUID invitationToken)
    {
        LeaseInvitationDetailsDTO leaseInvitationDetailsDTO = leaseInvitationService.getInvitationDetails(invitationToken);
        return ResponseEntity.status(200)
          .body(ApiResponse.success(leaseInvitationDetailsDTO, 200));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LeaseInvitationDetailsDTO>> createInvitation(@Valid @RequestBody LeaseInvitationCreateDTO leaseInvitationCreateDTO)
    {
        LeaseInvitationDetailsDTO leaseInvitationDetailsDTO = leaseInvitationService.createInvitation(leaseInvitationCreateDTO);
        return ResponseEntity.status(201)
          .body(ApiResponse.success(leaseInvitationDetailsDTO, 201));
    }

    @PostMapping("/{invitationToken}/tenant/accept")
    public ResponseEntity<ApiResponse<Void>> tenantAcceptInvitation(@PathVariable UUID invitationToken, @RequestParam Long userId)
    {
        leaseInvitationService.tenantAcceptInvitationInitiatedByLandlord(invitationToken, userId);
        return ResponseEntity.status(200)
                .body(ApiResponse.success(null, 200));
    }

    @PostMapping("/{invitationToken}/landlord/accept")
    public ResponseEntity<ApiResponse<Void>> landlordAcceptInvitation(@PathVariable UUID invitationToken, @RequestParam Long landlordId)
    {
        leaseInvitationService.landlordAcceptInvitationInitiatedByTenant(invitationToken, landlordId);
        return ResponseEntity.status(200)
          .body(ApiResponse.success(null, 200));
    }

    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<ApiResponse<List<LeaseInvitationDetailsDTO>>> getPendingInvitationsByPhoneNumber(@PathVariable String phoneNumber)
    {
        List<LeaseInvitationDetailsDTO> invitations = leaseInvitationService.getPendingInvitationsByPhoneNumber(phoneNumber);
        return ResponseEntity.status(200)
          .body(ApiResponse.success(invitations, 200));
    }
}
