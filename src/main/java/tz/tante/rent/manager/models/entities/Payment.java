package tz.tante.rent.manager.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tz.tante.rent.manager.enums.PaymentStatus;
import tz.tante.rent.manager.enums.PaymentType;
import tz.tante.rent.manager.enums.PaymentMethod;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity(name = "payments")
@Table(name = "payments")
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String referenceId = UUID.randomUUID().toString();

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal amount;

  private LocalDate paymentDate;

  @Enumerated(EnumType.STRING)
  private PaymentStatus status;

  @Enumerated(EnumType.STRING)
  private PaymentType type;

  @Enumerated(EnumType.STRING)
  private PaymentMethod method;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "lease_id")
  private Lease lease;
}