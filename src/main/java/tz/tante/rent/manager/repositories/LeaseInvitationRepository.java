package tz.tante.rent.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tz.tante.rent.manager.enums.LeaseInvitationStatus;
import tz.tante.rent.manager.models.entities.LeaseInvitation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeaseInvitationRepository extends JpaRepository<LeaseInvitation, Long>
{
  Optional<LeaseInvitation> findByToken(UUID token);

  List<LeaseInvitation> findByPhoneNumberAndStatus(String phoneNumber, LeaseInvitationStatus leaseInvitationStatus);
}
