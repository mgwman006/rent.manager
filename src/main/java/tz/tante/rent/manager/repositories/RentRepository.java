package tz.tante.rent.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.tante.rent.manager.models.entities.Rent;

@Repository
public interface RentRepository extends JpaRepository<Rent, Long>
{
}
