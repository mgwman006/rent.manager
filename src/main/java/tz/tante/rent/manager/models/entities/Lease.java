package tz.tante.rent.manager.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tz.tante.rent.manager.enums.LeaseStatus;
import tz.tante.rent.manager.enums.RentFrequency;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "leases")
public class Lease extends BaseEntity
{
  @Column(nullable = false, unique = true, updatable = false, length = 50)
  private String referenceNumber;

  @Column(nullable = false)
  private LocalDate startDate;

  @Column(nullable = false)
  private LocalDate endDate;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal rentAmount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RentFrequency rentFrequency;

  @Column(nullable = false)
  private boolean fullLeasePaymentRequired;

  @Column(nullable = false)
  private String currency;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private LeaseStatus status;

  private Long unitId;

  @Column(nullable = true)
  private Long tenantId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "rental_profile_id", nullable = false)
  private RentalProfile rentalProfile;

  @OneToMany(
    mappedBy = "lease",
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  private List<Payment> payments = new ArrayList<>();

  @OneToMany(
    mappedBy = "lease",
    cascade = CascadeType.ALL,
    fetch = FetchType.LAZY
  )
  private Set<TenantInvitation> tenantInvitations = new HashSet<>();

  public void addTenantInvitation(TenantInvitation invitation)
  {
    tenantInvitations.add(invitation);
    invitation.setLease(this);
  }

}
