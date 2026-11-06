package tz.tante.rent.manager.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.tante.rent.manager.enums.ViewingStatus;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
  name = "viewing_requests",
  uniqueConstraints = {
    @UniqueConstraint(name = "uk_viewing_request_slot_and_tenant", columnNames = {"slot_id", "tenant_user_id"})
  },
  indexes = {
    @Index(name = "idx_viewing_request_tenant", columnList = "tenant_user_id"),
    @Index(name = "idx_viewing_request_status", columnList = "status")
  }
)
public class ViewingRequest extends BaseEntity {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "slot_id", nullable = false)
  private ViewingSlot slot;

  @Column(name = "tenant_user_id", nullable = false)
  private Long requesterUserId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 24)
  private ViewingStatus status = ViewingStatus.PENDING;

  @Column(name = "service_fee", nullable = false)
  private BigDecimal serviceFee;

  @Column(name = "fee_paid", nullable = false)
  private boolean feePaid = false;

  @Column(length = 500)
  private String notes;

  @OneToOne(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
  private ViewingOutcome outcome;

  public void setOutcome(ViewingOutcome outcome) {
    this.outcome = outcome;
    if (outcome != null) {
      outcome.setRequest(this);
    }
  }
}
