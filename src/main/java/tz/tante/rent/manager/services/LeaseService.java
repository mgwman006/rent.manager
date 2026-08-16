package tz.tante.rent.manager.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.tante.rent.manager.enums.LeaseStatus;
import tz.tante.rent.manager.enums.TenantInvitationStatus;
import tz.tante.rent.manager.exceptions.ResourceNotFoundException;
import tz.tante.rent.manager.models.dtos.requests.leases.LeaseCreateDTO;
import tz.tante.rent.manager.models.dtos.responses.LeaseDetailsDTO;
import tz.tante.rent.manager.models.entities.Lease;
import tz.tante.rent.manager.models.entities.RentalProfile;
import tz.tante.rent.manager.models.entities.Tenant;
import tz.tante.rent.manager.models.entities.TenantInvitation;
import tz.tante.rent.manager.repositories.LeaseRepository;
import tz.tante.rent.manager.repositories.RentalProfileRepository;
import tz.tante.rent.manager.repositories.TenantInvitationRepository;
import tz.tante.rent.manager.repositories.TenantRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class LeaseService
{
  private final LeaseRepository leaseRepository;
  private final RentalProfileRepository rentalProfileRepository;
  private final TenantRepository tenantRepository;
  private final TenantInvitationRepository tenantInvitationRepository;

  public List<LeaseDetailsDTO> getLeasesByRentalProfile(Long rentalProfileId)
  {
    List<Lease> leases = leaseRepository.findByRentalProfileId(rentalProfileId);
    return leases.stream()
      .map(this::getLeaseDetailsDTO)
      .toList();
  }

  @Transactional
  public LeaseDetailsDTO createLease(LeaseCreateDTO leaseCreateDTO)
  {
    RentalProfile rentalProfile = rentalProfileRepository.findById(leaseCreateDTO.rentalProfileId())
      .orElseThrow(() -> new ResourceNotFoundException("Rental profile with id " + leaseCreateDTO.rentalProfileId() + " not found"));


    Lease lease = new Lease();
    lease.setStartDate(leaseCreateDTO.startDate().toLocalDate());
    lease.setEndDate(leaseCreateDTO.endDate().toLocalDate());
    lease.setRentAmount(leaseCreateDTO.rentAmount());
    lease.setCurrency(leaseCreateDTO.currency());
    lease.setRentPeriod(leaseCreateDTO.rentPeriod());
    lease.setStatus(LeaseStatus.PENDING);

    rentalProfile.addLease(lease);

    if (leaseCreateDTO.tenantId() != null)
    {
      Tenant tenant = tenantRepository.findById(leaseCreateDTO.tenantId())
        .orElseThrow(() -> new ResourceNotFoundException("Tenant with id " + leaseCreateDTO.tenantId() + " not found"));
      lease.setTenant(tenant);
    }
    else
    {
      //sendInvitationToTenant(leaseCreateDTO.tenantFirstName(), leaseCreateDTO.tenantLastName(), leaseCreateDTO.tenantPhoneNumber());

      TenantInvitation tenantInvitation = new TenantInvitation();
      tenantInvitation.setFirstName(leaseCreateDTO.tenantFirstName());
      tenantInvitation.setLastName(leaseCreateDTO.tenantLastName());
      tenantInvitation.setPhoneNumber(leaseCreateDTO.tenantPhoneNumber());
      tenantInvitation.setCreatedAt(LocalDateTime.now());
      tenantInvitation.setExpiresAt(LocalDateTime.now().plusDays(7)); // Set expiration date for the invitation
      tenantInvitation.setStatus(TenantInvitationStatus.PENDING);
      tenantInvitation = tenantInvitationRepository.save(tenantInvitation);
      lease.addTenantInvitation(tenantInvitation);
    }

    Lease savedLease = leaseRepository.save(lease);
    return getLeaseDetailsDTO(savedLease);
  }



  private LeaseDetailsDTO getLeaseDetailsDTO(Lease lease)
  {
    return new LeaseDetailsDTO(
      lease.getReferenceNumber(),
      lease.getId(),
      lease.getStartDate().toString(),
      lease.getEndDate().toString(),
      lease.getRentAmount(),
      lease.getStatus().name(),
      lease.getTenant() != null ? lease.getTenant().getId() : null
    );
  }

  private void sendInvitationToTenant(String firstName, String lastName, String phoneNumber)
  {
    // Implement the logic to send an invitation to the tenant
    // This could involve sending an email or SMS with a link to create an account
  }
}
