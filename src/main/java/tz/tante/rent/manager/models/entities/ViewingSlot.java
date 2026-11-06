package tz.tante.rent.manager.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.tante.rent.manager.enums.ViewingSlotStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
  name = "viewing_slots",
  indexes = {
    @Index(name = "idx_viewing_slots_rental_profile", columnList = "rental_profile_id"),
    @Index(name = "idx_viewing_slots_property", columnList = "property_id")
  }
)
public class ViewingSlot extends BaseEntity {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "rental_profile_id", nullable = false)
  private RentalProfile rentalProfile;

  @Column(name = "property_id", nullable = false)
  private Long propertyId;

  @Column(name = "start_time", nullable = false)
  private LocalDateTime startTime;

  @Column(name = "end_time", nullable = false)
  private LocalDateTime endTime;

  @Column(name = "service_fee", nullable = false)
  private BigDecimal serviceFee;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 24)
  private ViewingSlotStatus status = ViewingSlotStatus.AVAILABLE;

  @Column(length = 500)
  private String notes;

  @OneToMany(mappedBy = "slot", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ViewingBooking> bookings = new ArrayList<>();

  public void addBooking(ViewingBooking booking) {
    bookings.add(booking);
    booking.setSlot(this);
  }
}
