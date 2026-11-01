package tz.tante.rent.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tz.tante.rent.manager.enums.LeaseInvitationStatus;
import tz.tante.rent.manager.models.entities.LeaseInvitation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeaseInvitationRepository extends JpaRepository<LeaseInvitation, Long>
{
  Optional<LeaseInvitation> findByToken(UUID token);

  List<LeaseInvitation> findByPhoneNumberAndStatus(String phoneNumber, LeaseInvitationStatus leaseInvitationStatus);

  List<LeaseInvitation> findByStatus(LeaseInvitationStatus leaseInvitationStatus);

  @Query("SELECT i FROM LeaseInvitation i WHERE i.status = :status AND (i.nextRetryAt IS NULL OR i.nextRetryAt <= :nextRetryAt) ORDER BY i.id ASC")
  List<LeaseInvitation> findDueInvitations(@Param("status") LeaseInvitationStatus status, @Param("nextRetryAt") LocalDateTime nextRetryAt);
}
