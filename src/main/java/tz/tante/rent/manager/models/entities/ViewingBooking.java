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
  name = "viewing_bookings",
  uniqueConstraints = {
    @UniqueConstraint(name = "uk_viewing_booking_slot_and_requester", columnNames = {"slot_id", "requester_user_id"})
  },
  indexes = {
    @Index(name = "idx_viewing_booking_requester", columnList = "requester_user_id"),
    @Index(name = "idx_viewing_booking_status", columnList = "status")
  }
)
public class ViewingBooking extends BaseEntity {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "slot_id", nullable = false)
  private ViewingSlot slot;

  @Column(name = "requester_user_id", nullable = false)
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

  @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
  private ViewingOutcome outcome;

  public void setOutcome(ViewingOutcome outcome) {
    this.outcome = outcome;
    if (outcome != null) {
      outcome.setBooking(this);
    }
  }
}
