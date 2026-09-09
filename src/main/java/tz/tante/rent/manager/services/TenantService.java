package tz.tante.rent.manager.services;

import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import tz.tante.rent.manager.models.dtos.responses.TenantDetailsDTO;
import tz.tante.rent.manager.repositories.TenantRepository;

@Service
@Setter
@AllArgsConstructor
public class TenantService
{
  private final TenantRepository tenantRepository;

  public TenantDetailsDTO getTenantDetailsByUserId(Long userId)
  {
    return tenantRepository.findByUserId(userId)
      .map(tenant -> new TenantDetailsDTO(
        tenant.getId(),
        tenant.getUserId(),
        tenant.getFirstName(),
        tenant.getLastName(),
        tenant.getEmail(),
        tenant.getPhoneNumber()
      ))
      .orElseThrow(() -> new RuntimeException("Tenant not found for userId: " + userId));
  }

}
