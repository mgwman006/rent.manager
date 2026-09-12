package tz.tante.rent.manager.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.tante.rent.manager.enums.LeaseStatus;
import tz.tante.rent.manager.enums.PaymentStatus;
import tz.tante.rent.manager.enums.RentFrequency;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static tz.tante.rent.manager.utilities.Constant.NOT_FOUND;

@Service
@AllArgsConstructor
public class LeaseService
{
  private final LeaseRepository leaseRepository;
  private final RentalProfileRepository rentalProfileRepository;
  private final TenantRepository tenantRepository;
  private final LeaseSequenceRepository leaseSequenceRepository;


  public List<LeaseDetailsDTO> getActiveLeasesByTenant(Long tenantId)
  {
    List<Lease> leases = leaseRepository.findByTenantIdAndStatus(tenantId, LeaseStatus.ACTIVE);
    return leases.stream()
      .map(this::getLeaseDetailsDTO)
      .toList();
  }

  public List<LeaseDetailsDTO> getAllLeases()
  {
    List<Lease> leases = leaseRepository.findAll();
    return leases.stream()
      .map(this::getLeaseDetailsDTO)
      .toList();
  }
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
      .orElseThrow(() -> new ResourceNotFoundException("Rental profile with id " + leaseCreateDTO.rentalProfileId() + NOT_FOUND));

    int currentYear = LocalDateTime.now(ZoneId.of("UTC")).getYear();
    LeaseSequence leaseSequence = leaseSequenceRepository.findForUpdate(rentalProfile.getId(), currentYear)
      .orElseGet(() -> createSequence(rentalProfile.getId(),currentYear));

    Long nextSequence = leaseSequence.getLastSequence() + 1;
    leaseSequence.setLastSequence(nextSequence);

    Lease lease = new Lease();
    lease.setStartDate(leaseCreateDTO.startDate());
    lease.setEndDate(leaseCreateDTO.endDate());
    lease.setRentAmount(leaseCreateDTO.rentAmount());
    lease.setCurrency(leaseCreateDTO.currency());
    lease.setRentFrequency(leaseCreateDTO.rentFrequency());
    lease.setFullLeasePaymentRequired(leaseCreateDTO.fullLeasePaymentRequired());
    lease.setStatus(LeaseStatus.PENDING);
    lease.setReferenceNumber(String.format("LS-RP%d-%d-%06d", rentalProfile.getId(), currentYear, nextSequence));
    lease.setUnitId(leaseCreateDTO.unitId());
    lease.setTenantId(null);

    rentalProfile.addLease(lease);

    if (leaseCreateDTO.tenantId() != null)
    {
      Tenant tenant = tenantRepository.findById(leaseCreateDTO.tenantId())
        .orElseThrow(() -> new ResourceNotFoundException("Tenant with id " + leaseCreateDTO.tenantId() + NOT_FOUND));
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

    BigDecimal totalPaymentAmount = getTotalPaymentAmount(
      lease.getRentAmount(),
      lease.getRentFrequency(),
      lease.getStartDate(),
      lease.getEndDate()
    );

    BigDecimal amountPaid = lease.getPayments()
      .stream()
      .filter(payment -> payment.getStatus() == PaymentStatus.COMPLETED)
      .map(Payment::getAmount)
      .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal balance = totalPaymentAmount.subtract(amountPaid);

    return new LeaseDetailsDTO(
      lease.getReferenceNumber(),
      lease.getId(),
      lease.getStartDate().toString(),
      lease.getEndDate().toString(),
      lease.getRentAmount(),
      lease.getCurrency(),
      lease.getRentFrequency(),
      lease.isFullLeasePaymentRequired(),
      totalPaymentAmount,
      amountPaid,
      balance,
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

  private BigDecimal getTotalPaymentAmount(
    BigDecimal rentAmount,
    RentFrequency rentFrequency,
    LocalDate startDate,
    LocalDate endDate
  ) {
    if (startDate.isAfter(endDate)) {
      throw new IllegalArgumentException("Start date cannot be after end date");
    }

    long periods = switch (rentFrequency) {
      case DAILY -> ChronoUnit.DAYS.between(startDate, endDate) + 1;

      case WEEKLY -> ChronoUnit.WEEKS.between(startDate, endDate) + 1;

      case MONTHLY -> ChronoUnit.MONTHS.between(
        YearMonth.from(startDate),
        YearMonth.from(endDate)
      ) + 1;

      case YEARLY -> ChronoUnit.YEARS.between(
        startDate,
        endDate
      ) + 1;
    };

    return rentAmount
      .multiply(BigDecimal.valueOf(periods));
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
