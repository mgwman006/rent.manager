package tz.tante.rent.manager.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.tante.rent.manager.enums.RentalProfileType;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "rental_profiles")
public class RentalProfile extends BaseEntity
{
  private String name;

  @Column(unique = true, nullable = false)
  private String phoneNumber;

  @Column(unique = true)
  private String email;

  @Column(unique = true)
  private Long organizationId;

  @Column(unique = true, nullable = false)
  private Long userId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private RentalProfileType type;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal viewingServiceFee;

  @OneToMany(mappedBy = "rentalProfile", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private Set<RentReceivingAccount> rentReceivingAccounts = new HashSet<>();

  @OneToMany(mappedBy = "rentalProfile", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private Set<Lease> leases = new HashSet<>();

  @OneToMany(mappedBy = "rentalProfile", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private Set<ViewingBooking> viewingBookings = new HashSet<>();

  public void addRentReceivingAccount(RentReceivingAccount rentReceivingAccount)
  {
    rentReceivingAccounts.add(rentReceivingAccount);
    rentReceivingAccount.setRentalProfile(this);
  }

  public void addLease(Lease lease)
  {
    leases.add(lease);
    lease.setRentalProfile(this);
  }

  public void addViewingBooking(ViewingBooking viewingBooking)
  {
    viewingBookings.add(viewingBooking);
    viewingBooking.setRentalProfile(this);
  }

}
