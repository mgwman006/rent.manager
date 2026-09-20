package tz.tante.rent.manager.models.entities;

import jakarta.persistence.*;
import lombok.*;
import tz.tante.rent.manager.enums.LeaseInitiator;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
  @Column(nullable = false, length = 20)
  private LeaseStatus status;

  private Long unitId;

  @Column(nullable = true)
  private Long tenantId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private LeaseInitiator initiatedBy;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "rental_profile_id", nullable = false)
  private RentalProfile rentalProfile;

  @OneToMany(
    mappedBy = "lease",
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  private List<PaymentBlock> paymentBlocks = new ArrayList<>();

  @OneToMany(
    mappedBy = "lease",
    cascade = CascadeType.ALL,
    fetch = FetchType.LAZY
  )
  private Set<LeaseInvitation> invitations = new HashSet<>();

  public void addInvitation(LeaseInvitation invitation)
  {
    invitations.add(invitation);
    invitation.setLease(this);
  }

}
