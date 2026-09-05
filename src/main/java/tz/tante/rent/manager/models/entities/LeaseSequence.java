package tz.tante.rent.manager.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
  name = "lease_sequences"
)
@Getter
@Setter
@NoArgsConstructor
public class LeaseSequence extends BaseEntity
{
  @Column(name = "rental_profile_id", nullable = false)
  private Long rentalProfileId;

  @Column(nullable = false)
  private Integer year;

  @Column(name = "last_sequence", nullable = false)
  private Long lastSequence = 0L;
}