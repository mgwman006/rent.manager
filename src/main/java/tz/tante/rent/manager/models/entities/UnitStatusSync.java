package tz.tante.rent.manager.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tz.tante.rent.manager.enums.SyncStatus;
import tz.tante.rent.manager.enums.UnitStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "unit_status_sync")
@Setter
@Getter
public class UnitStatusSync extends BaseEntity
{
  @Column(nullable = false)
  private Long unitId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UnitStatus targetStatus;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private SyncStatus status = SyncStatus.PENDING;

  @Column(nullable = false)
  private int attempts = 0;

  @Column(nullable = false)
  private LocalDateTime nextRetryAt = LocalDateTime.now();

  @Column(columnDefinition = "TEXT")
  private String lastError;

  // Generate getters and setters using your IDE.
}
