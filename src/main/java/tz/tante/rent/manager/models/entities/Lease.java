package tz.tante.rent.manager.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tz.tante.rent.manager.enums.LeaseStatus;
import tz.tante.rent.manager.enums.RentPeriod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "leases")
public class Lease extends BaseEntity
{
  @Column(nullable = false)
  private LocalDate startDate;

  @Column(nullable = false)
  private LocalDate endDate;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal rentAmount;

  @Enumerated(EnumType.STRING)
  private RentPeriod rentPeriod;

  // =========================
  // DEPOSIT RULES
  // =========================
  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal requiredDeposit;

  private BigDecimal paidDeposit;

  @Column(nullable = false)
  private String currency;

  @Enumerated(EnumType.STRING)
  private LeaseStatus status;


  private Long unitId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tenant_id", nullable = false)
  private Tenant tenant;

  @OneToMany(mappedBy = "lease", fetch = FetchType.LAZY)
  private List<Payment> payments = new ArrayList<>();

  // =========================
  // HELPER METHODS
  // =========================

  public boolean isDepositFullyPaid() {
    return paidDeposit != null &&
      paidDeposit.compareTo(requiredDeposit) >= 0;
  }
  public boolean isActive() {
    return status == LeaseStatus.ACTIVE &&
      LocalDate.now().isBefore(endDate);
  }
}
