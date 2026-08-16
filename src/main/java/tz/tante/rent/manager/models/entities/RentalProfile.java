package tz.tante.rent.manager.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.tante.rent.manager.enums.RentalProfileType;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table (name = "rental_profiles")
public class RentalProfile extends BaseEntity
{
  private String name;

  private String businessEmail;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RentalProfileType type;

  @OneToMany(mappedBy = "rentalProfile", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private Set<Membership> memberships = new HashSet<>();

  @OneToMany(mappedBy = "rentalProfile", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private Set<RentReceivingAccount> rentReceivingAccounts = new HashSet<>();

  @OneToMany(mappedBy = "rentalProfile", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private Set<Lease> leases = new HashSet<>();

  public void addMembership(Membership membership)
  {
    memberships.add(membership);
    membership.setRentalProfile(this);
  }

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

}
