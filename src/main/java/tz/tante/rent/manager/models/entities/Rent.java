package tz.tante.rent.manager.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.tante.rent.manager.enums.RentFrequency;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "rents")
public class Rent extends BaseEntity
{
  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal amount;

  @Column(nullable = false)
  private String currency;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RentFrequency frequency;

  @OneToMany(mappedBy = "rent", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Lease> leases = new ArrayList<>();

  public void addLease(Lease lease)
  {
    leases.add(lease);
    lease.setRent(this);
  }
}
