package tz.tante.rent.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tz.tante.rent.manager.models.entities.RentalProfile;

public interface RentalProfileRepository extends JpaRepository<RentalProfile, Long>
{
}
