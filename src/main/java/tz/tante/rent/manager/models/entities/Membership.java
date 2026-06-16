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
public class Membership extends BaseEntity
{
  private Long userId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "rental_profile_id", nullable = false)
  private RentalProfile rentalProfile;
}
