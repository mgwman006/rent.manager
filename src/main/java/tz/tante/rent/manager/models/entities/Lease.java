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
@Entity(name = "leases")
@Table(name = "leases")
public class Lease
{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private LocalDate startDate;

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

  private String currency;

  @Enumerated(EnumType.STRING)
  private LeaseStatus status;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "unit_id", nullable = false)
  private Unit unit;

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
    return status == LeaseStatus.ACTIVE;
  }
}
