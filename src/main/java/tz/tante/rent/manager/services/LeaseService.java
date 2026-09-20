package tz.tante.rent.manager.services;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.tante.rent.manager.engines.rent.RentCalculator;
import tz.tante.rent.manager.enums.*;
import tz.tante.rent.manager.exceptions.ResourceNotFoundException;
import tz.tante.rent.manager.exceptions.TanteException;
import tz.tante.rent.manager.models.dtos.requests.leases.LeaseCreateDTO;
import tz.tante.rent.manager.models.dtos.responses.LeaseDetailsDTO;
import tz.tante.rent.manager.models.dtos.responses.RentDTO;
import tz.tante.rent.manager.models.dtos.responses.TenantDetailsDTO;
import tz.tante.rent.manager.models.dtos.responses.LeaseInvitationDetailsDTO;
import tz.tante.rent.manager.models.entities.*;
import tz.tante.rent.manager.repositories.*;
import static tz.tante.rent.manager.utilities.Constant.NOT_FOUND;


import java.time.LocalDate;
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
  private final RentRepository rentRepository;


  public List<LeaseDetailsDTO> getActiveLeasesByTenant(Long tenantId)
  {
    List<Lease> leases = leaseRepository.findByTenantIdAndStatus(tenantId, LeaseStatus.ACTIVE);
    return leases.stream()
      .filter(lease -> lease.getEndDate().isAfter(LocalDate.now(ZoneId.of("UTC"))))
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
    RentCalculator.generatePaymentBlocksForLease(lease);

    rentalProfile.addLease(lease);


    LeaseInvitation leaseInvitation = createLeaseInvitation(
      leaseCreateDTO.tenantFirstName(),
      leaseCreateDTO.tenantLastName(),
      leaseCreateDTO.tenantPhoneNumber()
    );

    lease.addInvitation(leaseInvitation);

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

    tenantRepository.findById(leaseCreateDTO.tenantId())
      .orElseThrow(() -> new ResourceNotFoundException("Tenant with id " + leaseCreateDTO.tenantId() + NOT_FOUND));

    int currentYear = LocalDateTime.now(ZoneId.of("UTC")).getYear();
    LeaseSequence leaseSequence = leaseSequenceRepository.findForUpdate(currentYear)
      .orElseGet(() -> createSequence(currentYear));

    Long nextSequence = leaseSequence.getLastSequence() + 1;
    leaseSequence.setLastSequence(nextSequence);

    Lease lease = createLeaseFromDTO(leaseCreateDTO, LeaseInitiator.TENANT);
    lease.setReferenceNumber(String.format("LS-YR%d-%06d", currentYear, nextSequence));
    RentCalculator.generatePaymentBlocksForLease(lease);

    LeaseInvitation leaseInvitation = createLeaseInvitation(
      leaseCreateDTO.landlordFirstName(),
      leaseCreateDTO.landlordLastName(),
      leaseCreateDTO.landlordPhoneNumber()
    );

    lease.addInvitation(leaseInvitation);


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


    return new LeaseDetailsDTO(
      lease.getReferenceNumber(),
      lease.getId(),
      lease.getInitiatedBy(),
      lease.getStartDate().toString(),
      lease.getEndDate().toString(),
      lease.getRent() != null ?
      new RentDTO(
        lease.getRent().getId(),
        lease.getRent().getAmount(),
        lease.getRent().getCurrency(),
        lease.getRent().getFrequency().name()
      ) : null,
      lease.isFullLeasePaymentRequired(),
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
    lease.setFullLeasePaymentRequired(leaseCreateDTO.fullLeasePaymentRequired());
    lease.setStatus(initiator == LeaseInitiator.LANDLORD ? LeaseStatus.PENDING_TENANT_APPROVAL : LeaseStatus.PENDING_LANDLORD_APPROVAL);
    lease.setUnitId(leaseCreateDTO.unitId());

    if (leaseCreateDTO.tenantId() != null)
    {
      lease.setTenantId(leaseCreateDTO.tenantId());
    }

    Rent rent = rentRepository.findById(leaseCreateDTO.rent().id())
      .orElse(null);

    if (rent == null)
    {
      rent = new Rent();
      rent.setAmount(leaseCreateDTO.rent().amount());
      rent.setCurrency(leaseCreateDTO.rent().currency());
      rent.setFrequency(leaseCreateDTO.rent().frequency());
    }

    rent.addLease(lease);
    return lease;
  }

  private LeaseInvitation createLeaseInvitation(String firstName, String lastName, String phoneNumber)
  {
    LeaseInvitation leaseInvitation = new LeaseInvitation();
    leaseInvitation.setFirstName(firstName);
    leaseInvitation.setLastName(lastName);
    leaseInvitation.setPhoneNumber(phoneNumber);
    leaseInvitation.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC")));
    leaseInvitation.setExpiresAt(LocalDateTime.now(ZoneId.of("UTC")).plusDays(7)); // Set expiration date for the invitation
    leaseInvitation.setStatus(LeaseInvitationStatus.PENDING);
    return leaseInvitation;
  }
}
