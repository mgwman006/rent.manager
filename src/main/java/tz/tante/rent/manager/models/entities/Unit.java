package tz.tante.rent.manager.models.entities;

import tz.tante.rent.manager.enums.UnitStatus;
import tz.tante.rent.manager.enums.UnitType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity(name = "units")
@Table(name = "units")
public class Unit
{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String unitNumber;

  private BigDecimal rentAmount;

  @Enumerated(EnumType.STRING)
  private UnitType type;

  @Enumerated(EnumType.STRING)
  private UnitStatus status;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "property_id")
  private Property property;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "landlord_id")
  private Landlord landlord;

  @OneToMany(mappedBy = "unit")
  private List<Lease> leases = new ArrayList<>();
}
