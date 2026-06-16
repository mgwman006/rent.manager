package tz.tante.rent.manager.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tenants")
public class Tenant extends BaseEntity
{

  private Long userId;

  @OneToMany(mappedBy = "tenant", fetch = FetchType.LAZY)
  private Set<Lease> leases = new HashSet<>();
}