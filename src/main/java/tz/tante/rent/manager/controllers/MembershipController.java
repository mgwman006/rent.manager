package tz.tante.rent.manager.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tz.tante.rent.manager.models.dtos.ApiResponse;
import tz.tante.rent.manager.models.dtos.responses.MembershipDetailsDTO;
import tz.tante.rent.manager.services.MembershipService;

import java.util.List;

@RestController
@RequestMapping("/v1/memberships")
@AllArgsConstructor
public class MembershipController
{
  private final MembershipService membershipService;

  @GetMapping("/{userId}")
  public ResponseEntity<ApiResponse<List<MembershipDetailsDTO>>> getMembershipsByUserId(@PathVariable Long userId)
  {
    List<MembershipDetailsDTO> memberships = membershipService.getMembershipsByUserId(userId);
    return ResponseEntity.status(HttpStatus.OK)
      .body(ApiResponse.success(memberships, HttpStatus.OK.value()));
  }

  @GetMapping("phone/{phoneNumber}")
  public ResponseEntity<ApiResponse<List<MembershipDetailsDTO>>> getMembershipsByPhoneNumber(@PathVariable String phoneNumber)
  {
    List<MembershipDetailsDTO> memberships = membershipService.getMembershipsByPhoneNumber(phoneNumber);
    return ResponseEntity.status(HttpStatus.OK)
      .body(ApiResponse.success(memberships, HttpStatus.OK.value()));
  }
}
