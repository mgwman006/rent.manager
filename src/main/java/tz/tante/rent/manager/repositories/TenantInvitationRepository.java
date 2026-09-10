package tz.tante.rent.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tz.tante.rent.manager.enums.TenantInvitationStatus;
import tz.tante.rent.manager.models.entities.TenantInvitation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TenantInvitationRepository extends JpaRepository<TenantInvitation, Long>
{
  Optional<TenantInvitation> findByToken(UUID token);

  List<TenantInvitation> findByPhoneNumberAndStatus(String phoneNumber, TenantInvitationStatus tenantInvitationStatus);
}
