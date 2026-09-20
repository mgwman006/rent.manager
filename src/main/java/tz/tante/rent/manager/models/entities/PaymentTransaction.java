package tz.tante.rent.manager.models.entities;

import jakarta.persistence.*;
import lombok.*;
import tz.tante.rent.manager.enums.PaymentStatus;
import tz.tante.rent.manager.enums.PaymentType;
import tz.tante.rent.manager.enums.PaymentMethod;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransaction extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "payment_block_id", nullable = false)
  private PaymentBlock paymentBlock;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal amount;

  @Column(nullable = false)
  private LocalDateTime transactionDate;

  @Column(nullable = false, unique = true, length = 100)
  private String reference;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private PaymentMethod method;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private PaymentStatus status;
}