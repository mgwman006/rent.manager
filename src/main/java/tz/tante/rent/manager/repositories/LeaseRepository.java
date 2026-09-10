package tz.tante.rent.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tz.tante.rent.manager.enums.LeaseStatus;
import tz.tante.rent.manager.models.entities.Lease;
import java.util.List;

public interface LeaseRepository extends JpaRepository<Lease, Long>
{
  List<Lease> findByRentalProfileId(Long rentalProfileId);

  List<Lease> findByTenantIdAndStatus(Long tenantId, LeaseStatus leaseStatus);
}
