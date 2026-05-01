package tz.tante.rent.manager.models.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tz.tante.rent.manager.enums.RoleName;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role extends BaseEntity
{
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, unique = true)
  private RoleName name;

  @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
  private Set<RentalProfileMembership>  rentalProfileMemberships = new HashSet<>();
}
