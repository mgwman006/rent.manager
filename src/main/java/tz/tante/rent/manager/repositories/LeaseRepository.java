package tz.tante.rent.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tz.tante.rent.manager.enums.LeaseStatus;
import tz.tante.rent.manager.models.entities.Lease;

import java.time.LocalDate;
import java.util.List;

public interface LeaseRepository extends JpaRepository<Lease, Long>
{
  List<Lease> findByRentalProfileId(Long rentalProfileId);

  List<Lease> findByTenantIdAndStatus(Long tenantId, LeaseStatus leaseStatus);

  List<Lease> findByRentalProfileIdAndStatus(Long rentalProfileId, LeaseStatus status);

  @Query("SELECT l FROM Lease l WHERE l.status = :status AND l.endDate < :today")
  List<Lease> findActiveLeasesPastEndDate( @Param("today") LocalDate today, @Param("status") LeaseStatus status);

  List<Lease> findByTenantId(Long tenantId);
}


