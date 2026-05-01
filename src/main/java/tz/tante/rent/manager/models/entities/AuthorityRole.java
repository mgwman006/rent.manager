package tz.tante.rent.manager.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tz.tante.rent.manager.enums.AuthorityRoleName;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "authority_roles")
public class AuthorityRole extends BaseEntity
{
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, unique = true)
  private AuthorityRoleName name;

  @ManyToMany(mappedBy = "authorityRoles")
  private Set<Account> accounts = new HashSet<>();
}
