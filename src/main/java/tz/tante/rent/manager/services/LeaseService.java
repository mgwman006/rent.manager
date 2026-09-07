package tz.tante.rent.manager.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.tante.rent.manager.enums.LeaseStatus;
import tz.tante.rent.manager.enums.PaymentPeriod;
import tz.tante.rent.manager.enums.RentPeriod;
import tz.tante.rent.manager.enums.TenantInvitationStatus;
import tz.tante.rent.manager.exceptions.ResourceNotFoundException;
import tz.tante.rent.manager.models.dtos.requests.leases.LeaseCreateDTO;
import tz.tante.rent.manager.models.dtos.responses.LeaseDetailsDTO;
import tz.tante.rent.manager.models.dtos.responses.TenantDetailsDTO;
import tz.tante.rent.manager.models.dtos.responses.TenantInvitationDetailsDTO;
import tz.tante.rent.manager.models.entities.*;
import tz.tante.rent.manager.repositories.LeaseRepository;
import tz.tante.rent.manager.repositories.LeaseSequenceRepository;
import tz.tante.rent.manager.repositories.RentalProfileRepository;
import tz.tante.rent.manager.repositories.TenantRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@AllArgsConstructor
public class LeaseService
{
  private final LeaseRepository leaseRepository;
  private final RentalProfileRepository rentalProfileRepository;
  private final TenantRepository tenantRepository;
  private final LeaseSequenceRepository leaseSequenceRepository;

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

    int currentYear = LocalDateTime.now(ZoneId.of("UTC")).getYear();
    LeaseSequence leaseSequence = leaseSequenceRepository.findForUpdate(rentalProfile.getId(), currentYear)
      .orElseGet(() -> createSequence(rentalProfile.getId(),currentYear));

    Long nextSequence = leaseSequence.getLastSequence() + 1;
    leaseSequence.setLastSequence(nextSequence);

    BigDecimal paymentAmount = getPaymentAmount(leaseCreateDTO.rentAmount(), leaseCreateDTO.rentPeriod(), leaseCreateDTO.paymentPeriod());
    BigDecimal amountPaid = BigDecimal.ZERO;
    BigDecimal balance = paymentAmount.subtract(amountPaid);


    Lease lease = new Lease();
    lease.setStartDate(leaseCreateDTO.startDate());
    lease.setEndDate(leaseCreateDTO.endDate());
    lease.setRentAmount(leaseCreateDTO.rentAmount());
    lease.setCurrency(leaseCreateDTO.currency());
    lease.setRentPeriod(leaseCreateDTO.rentPeriod());
    lease.setPaymentPeriod(leaseCreateDTO.paymentPeriod());
    lease.setPaymentAmount(paymentAmount);
    lease.setAmountPaid(amountPaid);
    lease.setBalance(balance);
    lease.setStatus(LeaseStatus.PENDING);
    lease.setReferenceNumber(String.format("LS-RP%d-%d-%06d", rentalProfile.getId(), currentYear, nextSequence));
    lease.setUnitId(leaseCreateDTO.unitId());
    lease.setTenantId(null);

    rentalProfile.addLease(lease);

    if (leaseCreateDTO.tenantId() != null)
    {
      Tenant tenant = tenantRepository.findById(leaseCreateDTO.tenantId())
        .orElseThrow(() -> new ResourceNotFoundException("Tenant with id " + leaseCreateDTO.tenantId() + " not found"));
      lease.setTenantId(tenant.getId());
    }
    else
    {
      //sendInvitationToTenant(leaseCreateDTO.tenantFirstName(), leaseCreateDTO.tenantLastName(), leaseCreateDTO.tenantPhoneNumber());

      TenantInvitation tenantInvitation = new TenantInvitation();
      tenantInvitation.setFirstName(leaseCreateDTO.tenantFirstName());
      tenantInvitation.setLastName(leaseCreateDTO.tenantLastName());
      tenantInvitation.setPhoneNumber(leaseCreateDTO.tenantPhoneNumber());
      tenantInvitation.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC")));
      tenantInvitation.setExpiresAt(LocalDateTime.now(ZoneId.of("UTC")).plusDays(7)); // Set expiration date for the invitation
      tenantInvitation.setStatus(TenantInvitationStatus.PENDING);
      lease.addTenantInvitation(tenantInvitation);
    }

    Lease savedLease = leaseRepository.save(lease);
    return getLeaseDetailsDTO(savedLease);
  }

  private LeaseSequence createSequence(Long rentalProfileId, int currentYear)
  {
    LeaseSequence leaseSequence = new LeaseSequence();
    leaseSequence.setRentalProfileId(rentalProfileId);
    leaseSequence.setYear(currentYear);
    leaseSequence.setLastSequence(0L);
    leaseSequence = leaseSequenceRepository.save(leaseSequence);
    return leaseSequence;
  }


  private LeaseDetailsDTO getLeaseDetailsDTO(Lease lease)
  {
    Tenant tenant = null;
    if (lease.getTenantId() != null)
    {
      tenant = tenantRepository.findById(lease.getTenantId()).orElse(null);
    }

    List<TenantInvitationDetailsDTO> tenantInvitations = lease.getTenantInvitations()
      .stream()
      .map(invitation -> mapTenantInvitationToDTO(lease.getId(), invitation))
      .toList();

    return new LeaseDetailsDTO(
      lease.getReferenceNumber(),
      lease.getId(),
      lease.getStartDate().toString(),
      lease.getEndDate().toString(),
      lease.getRentAmount(),
      lease.getCurrency(),
      lease.getRentPeriod(),
      lease.getPaymentPeriod(),
      lease.getPaymentAmount(),
      lease.getAmountPaid(),
      lease.getBalance(),
      lease.getStatus().name(),
      tenant != null ? new TenantDetailsDTO(
        tenant.getId(),
        tenant.getUserId(),
        tenant.getFirstName(),
        tenant.getLastName(),
        tenant.getEmail(),
        tenant.getPhoneNumber()
      ) : null,
      tenantInvitations
    );
  }

  private void sendInvitationToTenant(String firstName, String lastName, String phoneNumber)
  {
    // Implement the logic to send an invitation to the tenant
    // This could involve sending an email or SMS with a link to create an account
  }

  private BigDecimal getPaymentAmount(BigDecimal rentAmount, RentPeriod rentPeriod, PaymentPeriod paymentPeriod)
  {
    int multiplier = 1;

    switch (rentPeriod)
    {
      case DAILY -> {
        switch (paymentPeriod)
        {
          case DAILY -> multiplier = 1;
          case WEEKLY -> multiplier = 7;
          case MONTHLY -> multiplier = 30;
          case YEARLY -> multiplier = 365;
          default -> throw new IllegalArgumentException("Invalid payment period for daily rent period");
        }
      }

      case WEEKLY -> {
        switch (paymentPeriod)
        {
          case WEEKLY -> multiplier = 1;
          case MONTHLY -> multiplier = 4;
          case YEARLY -> multiplier = 52;
          default -> throw new IllegalArgumentException("Invalid payment period for weekly rent period");
        }
      }

      case MONTHLY -> {
        switch (paymentPeriod)
        {
          case MONTHLY -> multiplier = 1;
          case SIX_MONTHS -> multiplier = 6;
          case YEARLY -> multiplier = 12;
          default -> throw new IllegalArgumentException("Invalid payment period for monthly rent period");
        }
      }

      case SIX_MONTHS -> {
        switch (paymentPeriod)
        {
          case SIX_MONTHS -> multiplier = 1;
          case YEARLY -> multiplier = 2;
          default -> throw new IllegalArgumentException("Invalid payment period for six months rent period");
        }
      }

      case YEARLY -> {
        switch (paymentPeriod)
        {
          case YEARLY -> multiplier = 1;
          default -> throw new IllegalArgumentException("Invalid payment period for yearly rent period");
        }
      }

      default -> throw new IllegalArgumentException("Invalid rent period");
    }

    return rentAmount.multiply(BigDecimal.valueOf(multiplier)).setScale(2, BigDecimal.ROUND_HALF_UP);
  }

  private TenantInvitationDetailsDTO mapTenantInvitationToDTO(Long leaseId, TenantInvitation invitation) {
    return new TenantInvitationDetailsDTO(
      leaseId,
      invitation.getId(),
      invitation.getFirstName(),
      invitation.getLastName(),
      invitation.getPhoneNumber(),
      invitation.getEmail(),
      invitation.getToken(),
      invitation.getStatus(),
      invitation.getExpiresAt(),
      invitation.getAcceptedAt(),
      invitation.getSentAt()
    );
  }

  public LeaseDetailsDTO getLeaseById(Long leaseId)
  {
    Lease lease = leaseRepository.findById(leaseId)
      .orElseThrow(() -> new ResourceNotFoundException("Lease with id " + leaseId + " not found"));
    return getLeaseDetailsDTO(lease);
  }
}
