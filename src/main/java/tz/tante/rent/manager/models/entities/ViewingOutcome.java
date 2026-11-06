package tz.tante.rent.manager.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.tante.rent.manager.enums.ViewingOutcomeType;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "viewing_outcomes")
public class ViewingOutcome extends BaseEntity {
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "viewing_request_id", nullable = false, unique = true)
  private ViewingRequest request;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 24)
  private ViewingOutcomeType type;

  @Column(length = 500)
  private String notes;

  @Column(name = "recorded_at", nullable = false)
  private LocalDateTime recordedAt;
}
