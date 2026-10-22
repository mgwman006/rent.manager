package tz.tante.rent.manager.services;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.tante.rent.manager.clients.PropertyManagerClient;
import tz.tante.rent.manager.enums.SyncStatus;
import tz.tante.rent.manager.models.entities.UnitStatusSync;
import tz.tante.rent.manager.repositories.UnitStatusSyncRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UnitStatusSyncService {

  private final UnitStatusSyncRepository unitStatusSyncRepository;
  private final PropertyManagerClient propertyManagerClient;

  @Scheduled(fixedDelay = 300000)
  @Transactional
  public void processPendingSyncs() {
    List<UnitStatusSync> pendingSyncs = unitStatusSyncRepository.findByStatusAndNextRetryAtLessThanEqualOrderByIdAsc(SyncStatus.PENDING, LocalDateTime.now());

    for (UnitStatusSync sync : pendingSyncs)
    {
      if (unitStatusSyncRepository.existsByUnitIdAndIdLessThanAndStatus(
        sync.getUnitId(),
        sync.getId(),
        SyncStatus.PENDING)) {
        log.debug(
          "Skipping sync because an earlier sync is pending: id={}, unitId={}",
          sync.getId(),
          sync.getUnitId()
        );
        continue;
      }

      try
      {
        propertyManagerClient.updateUnitStatus(sync.getUnitId(), sync.getTargetStatus());
        sync.setStatus(SyncStatus.COMPLETED);
        sync.setLastError(null);
        unitStatusSyncRepository.save(sync);
        log.info("Successfully synced unit status: id={}, unitId={}, targetStatus={}", sync.getId(), sync.getUnitId(), sync.getTargetStatus());
      }
      catch (Exception exception)
      {
        sync.setLastError(exception.getMessage());
        sync.setAttempts(sync.getAttempts() + 1);

        if (exception instanceof FeignException feignException
          && feignException.status() >= 400
          && feignException.status() < 500
          && feignException.status() != 429) {

          sync.setStatus(SyncStatus.FAILED);

          log.error(
            "Permanent unit sync failure: id={}, unitId={}, HTTP status={}",
            sync.getId(),
            sync.getUnitId(),
            feignException.status());

        }
        else
        {
          long delaySeconds = Math.min(30L * (1L << Math.min(sync.getAttempts() - 1, 7)), 3600L);

          sync.setNextRetryAt(LocalDateTime.now().plusSeconds(delaySeconds));

          log.warn("Transient unit sync failure: id={}, unitId={}, attempts={}, nextRetryAt={}", sync.getId(),
            sync.getUnitId(), sync.getAttempts(), sync.getNextRetryAt(), exception);
        }



        unitStatusSyncRepository.save(sync);
      }
    }
  }
}
