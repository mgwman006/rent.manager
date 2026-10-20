package tz.tante.rent.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tz.tante.rent.manager.enums.SyncStatus;
import tz.tante.rent.manager.models.entities.UnitStatusSync;

import java.time.LocalDateTime;
import java.util.List;

public interface UnitStatusSyncRepository extends JpaRepository<UnitStatusSync, Long>
{
  List<UnitStatusSync> findByStatusAndNextRetryAtLessThanEqualOrderByIdAsc(
    SyncStatus status,
    LocalDateTime nextRetryAt
  );

  boolean existsByUnitIdAndIdLessThanAndStatus(
    Long unitId,
    Long id,
    SyncStatus status
  );
}