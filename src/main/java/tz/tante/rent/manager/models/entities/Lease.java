package tz.tante.rent.manager.models.entities;

import jakarta.persistence.*;
import lombok.*;
import tz.tante.rent.manager.enums.LeaseInitiator;
import tz.tante.rent.manager.enums.LeaseStatus;

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

  @Column(nullable = false)
  private boolean fullLeasePaymentRequired;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private LeaseStatus status;

  private Long unitId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tenant_id")
  Tenant tenant;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private LeaseInitiator initiatedBy;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "rental_profile_id")
  private RentalProfile rentalProfile;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "rent_id")
  private Rent rent;

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

  public void replacePaymentBlocks(List<PaymentBlock> paymentBlocks) {
    this.paymentBlocks.clear();

    paymentBlocks.forEach(this::addPaymentBlock);
  }

  public void addPaymentBlock(PaymentBlock paymentBlock) {
    paymentBlock.setLease(this);
    this.paymentBlocks.add(paymentBlock);
  }

}
