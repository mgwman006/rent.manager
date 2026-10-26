package tz.tante.rent.manager.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.tante.rent.manager.enums.LeaseInvitationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "lease_invitations")
public class LeaseInvitation extends BaseEntity {

  @Column(nullable = false, unique = true)
  private UUID token = UUID.randomUUID();

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "lease_id", nullable = false)
  private Lease lease;

  @Column(nullable = false)
  private String firstName;

  @Column(nullable = false)
  private String lastName;

  @Column(nullable = false)
  private String phoneNumber;

  private String email;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private LeaseInvitationStatus status;

  @Column(nullable = false)
  private LocalDateTime expiresAt;

  private LocalDateTime acceptedAt;

  private LocalDateTime sentAt;
}