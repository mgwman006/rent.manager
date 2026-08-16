package tz.tante.rent.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tz.tante.rent.manager.models.entities.TenantInvitation;

public interface TenantInvitationRepository extends JpaRepository<TenantInvitation, Long>
{
}
