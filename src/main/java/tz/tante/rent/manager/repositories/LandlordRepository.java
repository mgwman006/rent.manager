package tz.tante.rent.manager.repositories;

import tz.tante.rent.manager.models.entities.RentalProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LandlordRepository extends JpaRepository<RentalProfile,Long>
{
}
