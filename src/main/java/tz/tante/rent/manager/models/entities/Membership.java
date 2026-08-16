package tz.tante.rent.manager.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tz.tante.rent.manager.enums.MembershipRole;

@Getter
@Setter
@Entity
@Table(name = "rental_profile_memberships")
public class Membership extends BaseEntity
{
  private Long userId;

  private String phoneNumber;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "rental_profile_id", nullable = false)
  private RentalProfile rentalProfile;

  @Enumerated(EnumType.STRING)
  private MembershipRole membershipRole;
}
