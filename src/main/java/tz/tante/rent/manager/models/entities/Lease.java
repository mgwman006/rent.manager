package tz.tante.rent.manager.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tz.tante.rent.manager.enums.LeaseStatus;
import tz.tante.rent.manager.enums.RentPeriod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
  private RentPeriod rentPeriod;

  @Column(nullable = false)
  private String currency;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private LeaseStatus status;

  private Long unitId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tenant_id")
  private Tenant tenant;

  @ManyToOne
  @JoinColumn(name = "rental_profile_id")
  RentalProfile rentalProfile;

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

  public void addTenantInvitation(TenantInvitation invitation) {
    tenantInvitations.add(invitation);
    invitation.setLease(this);
  }

  public boolean isActive()
  {
    LocalDate today = LocalDate.now();
    return status == LeaseStatus.ACTIVE
      && !today.isBefore(startDate)
      && !today.isAfter(endDate);
  }

  public String generateReferenceNumber()
  {
    return this.referenceNumber = String.format(
      "LS-RP%d-U%d-T%d-%s-%s",
      rentalProfile.getId(),
      unitId,
      tenant.getId(),
      startDate.format(DateTimeFormatter.BASIC_ISO_DATE),
      endDate.format(DateTimeFormatter.BASIC_ISO_DATE)
    );
  }
}
