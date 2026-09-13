package tz.tante.rent.manager.services;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.tante.rent.manager.enums.*;
import tz.tante.rent.manager.exceptions.ResourceNotFoundException;
import tz.tante.rent.manager.exceptions.TanteException;
import tz.tante.rent.manager.models.dtos.requests.leases.LeaseCreateDTO;
import tz.tante.rent.manager.models.dtos.responses.LeaseDetailsDTO;
import tz.tante.rent.manager.models.dtos.responses.TenantDetailsDTO;
import tz.tante.rent.manager.models.dtos.responses.LeaseInvitationDetailsDTO;
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
  public LeaseDetailsDTO createLeaseInitiatedByLandlord(LeaseCreateDTO leaseCreateDTO)
  {
    RentalProfile rentalProfile = rentalProfileRepository.findById(leaseCreateDTO.rentalProfileId())
      .orElseThrow(() -> new ResourceNotFoundException("Rental profile with id " + leaseCreateDTO.rentalProfileId() + NOT_FOUND));

    int currentYear = LocalDateTime.now(ZoneId.of("UTC")).getYear();
    LeaseSequence leaseSequence = leaseSequenceRepository.findForUpdate(currentYear)
      .orElseGet(() -> createSequence(currentYear));

    Long nextSequence = leaseSequence.getLastSequence() + 1;
    leaseSequence.setLastSequence(nextSequence);

    Lease lease = createLeaseFromDTO(leaseCreateDTO, LeaseInitiator.LANDLORD);
    lease.setReferenceNumber(String.format("LS-YR%d-%06d", currentYear, nextSequence));

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

      LeaseInvitation leaseInvitation = new LeaseInvitation();
      leaseInvitation.setFirstName(leaseCreateDTO.tenantFirstName());
      leaseInvitation.setLastName(leaseCreateDTO.tenantLastName());
      leaseInvitation.setPhoneNumber(leaseCreateDTO.tenantPhoneNumber());
      leaseInvitation.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC")));
      leaseInvitation.setExpiresAt(LocalDateTime.now(ZoneId.of("UTC")).plusDays(7)); // Set expiration date for the invitation
      leaseInvitation.setStatus(LeaseInvitationStatus.PENDING);
      lease.addInvitation(leaseInvitation);
    }

    Lease savedLease = leaseRepository.save(lease);
    return getLeaseDetailsDTO(savedLease);
  }

  @Transactional
  public LeaseDetailsDTO createLeaseInitiatedByTenant(LeaseCreateDTO leaseCreateDTO)
  {
    if (StringUtils.isBlank(leaseCreateDTO.landlordPhoneNumber()))
    {
      throw new TanteException("Landlord phone number is required when creating a lease initiated by tenant.");
    }

    Tenant tenant = tenantRepository.findById(leaseCreateDTO.tenantId())
      .orElseThrow(() -> new ResourceNotFoundException("Tenant with id " + leaseCreateDTO.tenantId() + NOT_FOUND));

    int currentYear = LocalDateTime.now(ZoneId.of("UTC")).getYear();
    LeaseSequence leaseSequence = leaseSequenceRepository.findForUpdate(currentYear)
      .orElseGet(() -> createSequence(currentYear));

    Long nextSequence = leaseSequence.getLastSequence() + 1;
    leaseSequence.setLastSequence(nextSequence);

    Lease lease = createLeaseFromDTO(leaseCreateDTO, LeaseInitiator.TENANT);
    lease.setReferenceNumber(String.format("LS-YR%d-%06d", currentYear, nextSequence));


    RentalProfile rentalProfile = rentalProfile = rentalProfileRepository.findByPhoneNumber(leaseCreateDTO.landlordPhoneNumber())
        .orElse(null);

    if (rentalProfile != null)
    {
      rentalProfile.addLease(lease);
    }
    else
    {
      //sendInvitationToTenant(leaseCreateDTO.tenantFirstName(), leaseCreateDTO.tenantLastName(), leaseCreateDTO.tenantPhoneNumber());

      LeaseInvitation leaseInvitation = new LeaseInvitation();
      leaseInvitation.setFirstName(leaseCreateDTO.landlordFirstName());
      leaseInvitation.setLastName(leaseCreateDTO.landlordLastName());
      leaseInvitation.setPhoneNumber(leaseCreateDTO.landlordPhoneNumber());
      leaseInvitation.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC")));
      leaseInvitation.setExpiresAt(LocalDateTime.now(ZoneId.of("UTC")).plusDays(7)); // Set expiration date for the invitation
      leaseInvitation.setStatus(LeaseInvitationStatus.PENDING);
      lease.addInvitation(leaseInvitation);
    }

    Lease savedLease = leaseRepository.save(lease);
    return getLeaseDetailsDTO(savedLease);
  }

  private LeaseSequence createSequence(int currentYear)
  {
    LeaseSequence leaseSequence = new LeaseSequence();
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

    List<LeaseInvitationDetailsDTO> tenantInvitations = lease.getInvitations()
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
      lease.getInitiatedBy(),
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

  private LeaseInvitationDetailsDTO mapTenantInvitationToDTO(Long leaseId, LeaseInvitation invitation) {
    return new LeaseInvitationDetailsDTO(
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

  private Lease createLeaseFromDTO(LeaseCreateDTO leaseCreateDTO, LeaseInitiator initiator)
  {
    Lease lease = new Lease();
    lease.setInitiatedBy(initiator);
    lease.setStartDate(leaseCreateDTO.startDate());
    lease.setEndDate(leaseCreateDTO.endDate());
    lease.setRentAmount(leaseCreateDTO.rentAmount());
    lease.setCurrency(leaseCreateDTO.currency());
    lease.setRentFrequency(leaseCreateDTO.rentFrequency());
    lease.setFullLeasePaymentRequired(leaseCreateDTO.fullLeasePaymentRequired());
    lease.setStatus(LeaseStatus.PENDING);
    lease.setUnitId(leaseCreateDTO.unitId());

    if (leaseCreateDTO.tenantId() != null)
    {
      lease.setTenantId(leaseCreateDTO.tenantId());
    }
    return lease;
  }
}
