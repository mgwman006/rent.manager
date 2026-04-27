package tz.tante.rent.manager.repositories;

import tz.tante.rent.manager.models.entities.Landlord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LandlordRepository extends JpaRepository<Landlord,Long>
{

  boolean existsByPhoneNumber(String phoneNumber);
  boolean existsByEmail(String email);

}
