package tz.tante.rent.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tz.tante.rent.manager.models.entities.Lease;

public interface LeaseRepository extends JpaRepository<Lease, Long>
{
}
