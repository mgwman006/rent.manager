package services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tz.tante.rent.manager.clients.PropertyManagerClient;
import tz.tante.rent.manager.enums.LeaseStatus;
import tz.tante.rent.manager.models.dtos.responses.LeaseDetailsDTO;
import tz.tante.rent.manager.models.entities.Lease;
import tz.tante.rent.manager.models.entities.RentalProfile;
import tz.tante.rent.manager.repositories.LeaseRepository;
import tz.tante.rent.manager.repositories.LeaseSequenceRepository;
import tz.tante.rent.manager.repositories.RentRepository;
import tz.tante.rent.manager.repositories.RentalProfileRepository;
import tz.tante.rent.manager.repositories.TenantRepository;
import tz.tante.rent.manager.repositories.UnitStatusSyncRepository;
import tz.tante.rent.manager.engines.rent.RentCalculator;
import tz.tante.rent.manager.services.LeaseService;
import tz.tante.rent.manager.exceptions.TanteException;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaseServiceAssignUnitTest
{
  @Mock private LeaseRepository leaseRepository;
  @Mock private RentalProfileRepository rentalProfileRepository;
  @Mock private TenantRepository tenantRepository;
  @Mock private LeaseSequenceRepository leaseSequenceRepository;
  @Mock private RentRepository rentRepository;
  @Mock private RentCalculator rentCalculator;
  @Mock private UnitStatusSyncRepository unitStatusSyncRepository;
  @Mock private PropertyManagerClient propertyManagerClient;

  private LeaseService leaseService;

  @BeforeEach
  void setUp()
  {
    leaseService = new LeaseService(
      leaseRepository,
      rentalProfileRepository,
      tenantRepository,
      leaseSequenceRepository,
      rentRepository,
      rentCalculator,
      unitStatusSyncRepository,
      propertyManagerClient
    );
  }

  @Test
  void assignsAvailableUnitFromLeaseRentalProfile()
  {
    Lease lease = lease();
    when(leaseRepository.findById(15L)).thenReturn(Optional.of(lease));
    when(propertyManagerClient.getUnitDetails(42L)).thenReturn(Map.of(
      "success", true,
      "data", Map.of("rentalProfileId", 7L, "status", "AVAILABLE")
    ));
    when(leaseRepository.save(lease)).thenReturn(lease);

    LeaseDetailsDTO updated = leaseService.assignUnit(15L, 42L);

    assertEquals(42L, updated.unitId());
    verify(leaseRepository).save(lease);
  }

  @Test
  void rejectsUnitFromAnotherRentalProfile()
  {
    Lease lease = lease();
    when(leaseRepository.findById(15L)).thenReturn(Optional.of(lease));
    when(propertyManagerClient.getUnitDetails(42L)).thenReturn(Map.of(
      "success", true,
      "data", Map.of("rentalProfileId", 8L, "status", "AVAILABLE")
    ));

    assertThrows(TanteException.class, () -> leaseService.assignUnit(15L, 42L));
    verify(leaseRepository, never()).save(any());
  }

  private Lease lease()
  {
    RentalProfile rentalProfile = new RentalProfile();
    rentalProfile.setId(7L);
    Lease lease = new Lease();
    lease.setId(15L);
    lease.setReferenceNumber("LEASE-15");
    lease.setStartDate(LocalDate.of(2026, 1, 1));
    lease.setEndDate(LocalDate.of(2026, 12, 31));
    lease.setStatus(LeaseStatus.PENDING_TENANT_APPROVAL);
    lease.setRentalProfile(rentalProfile);
    return lease;
  }
}
