package tz.tante.rent.manager.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;


import tz.tante.rent.manager.enums.ViewingBookingStatus;
import tz.tante.rent.manager.enums.ViewingPaymentStatus;



@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "viewing_bookings")
public class ViewingBooking extends BaseEntity {
  @Column(nullable = false, unique = true, length = 60)
  private String reference;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  private RentalProfile rentalProfile;

  @Column(nullable = false)
  private Long unitId;

  @Column(nullable = false)
  private LocalDateTime startTime;

  @Column(nullable = false)
  private LocalDateTime endTime;

  @Column(nullable = false)
  private Long requesterUserAccountId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 24)
  private ViewingBookingStatus status = ViewingBookingStatus.DRAFT;

  @Column(nullable = false)
  private BigDecimal serviceFee;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 24)
  private ViewingPaymentStatus paymentStatus = ViewingPaymentStatus.AWAITING_PAYMENT;

  @Column
  private LocalDateTime paymentUpdatedAt;

  @Column
  private LocalDateTime holdExpiresAt;

  @Column(length = 500)
  private String notes;

  @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
  private ViewingOutcome outcome;

  public void setOutcome(ViewingOutcome outcome)
  {
    this.outcome = outcome;
    if (outcome != null) {
      outcome.setBooking(this);
    }
  }
}
