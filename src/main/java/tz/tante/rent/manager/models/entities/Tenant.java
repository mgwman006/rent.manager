package tz.tante.rent.manager.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
    name = "tenants",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_tenants_user_id", columnNames = "user_id"),
        @UniqueConstraint(name = "uk_tenants_phone_number", columnNames = "phone_number")
    }
)
public class Tenant extends BaseEntity
{
  @Column(name = "user_id", nullable = false, unique = true)
  private Long userId;
  private String firstName;
  private String lastName;
  private String email;
  @Column(name = "phone_number", nullable = false, unique = true)
  private String phoneNumber;

  @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Lease> leases = new ArrayList<>();

  public void addLease(Lease lease)
  {
    leases.add(lease);
    lease.setTenant(this);
  }
}