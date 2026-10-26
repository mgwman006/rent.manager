package tz.tante.rent.manager.services;

import lombok.AllArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tz.tante.rent.manager.exceptions.ResourceNotFoundException;
import tz.tante.rent.manager.models.dtos.requests.Tenants.TenantContactUpdateDTO;
import tz.tante.rent.manager.models.dtos.responses.TenantDetailsDTO;
import tz.tante.rent.manager.models.entities.Tenant;
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

  public TenantDetailsDTO updateTenantContact(Long id, TenantContactUpdateDTO dto)
  {
    Tenant tenant = tenantRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Tenant not found for id: " + id));

    tenant.setPhoneNumber(dto.phoneNumber());
    if (StringUtils.isNotBlank(dto.email())) {
      tenant.setEmail(dto.email());
    }
    tenant = tenantRepository.save(tenant);

    return new TenantDetailsDTO(
      tenant.getId(),
      tenant.getUserId(),
      tenant.getFirstName(),
      tenant.getLastName(),
      tenant.getEmail(),
      tenant.getPhoneNumber()
    );
  }
}
