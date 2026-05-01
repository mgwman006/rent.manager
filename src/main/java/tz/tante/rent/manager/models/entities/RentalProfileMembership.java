package tz.tante.rent.manager.models.entities;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rental_profile_memberships")
public class RentalProfileMembership extends BaseEntity
{
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "rental_profile_id", nullable = false)
  private RentalProfile rentalProfile;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
    name = "rental_profile_member_roles",
    joinColumns = @JoinColumn(name = "membership_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id")
  )
  private Set<Role> roles = new HashSet<>();
}
