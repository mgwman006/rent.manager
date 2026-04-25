package tz.tante.rent.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tz.tante.rent.manager.models.entities.Property;

public interface PropertyRepository extends JpaRepository<Property, Long>
{
}
