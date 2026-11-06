package tz.tante.rent.manager.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.tante.rent.manager.enums.ViewingBookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
  name = "viewing_bookings",
  indexes = {
    @Index(name = "idx_viewing_booking_rental_profile", columnList = "rental_profile_id"),
    @Index(name = "idx_viewing_booking_property", columnList = "property_id"),
    @Index(name = "idx_viewing_booking_requester", columnList = "requester_user_id"),
    @Index(name = "idx_viewing_booking_status", columnList = "status")
  }
)
public class ViewingBooking extends BaseEntity {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "rental_profile_id", nullable = false)
  private RentalProfile rentalProfile;

  @Column(name = "property_id", nullable = false)
  private Long propertyId;

  @Column(name = "start_time", nullable = false)
  private LocalDateTime startTime;

  @Column(name = "end_time", nullable = false)
  private LocalDateTime endTime;

  @Column(name = "requester_user_id", nullable = false)
  private Long requesterUserId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 24)
  private ViewingBookingStatus status = ViewingBookingStatus.PENDING;

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
