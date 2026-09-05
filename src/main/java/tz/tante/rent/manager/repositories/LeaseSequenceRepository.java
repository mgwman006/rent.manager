package tz.tante.rent.manager.repositories;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tz.tante.rent.manager.models.entities.LeaseSequence;

import java.util.Optional;

@Repository
public interface LeaseSequenceRepository extends JpaRepository<LeaseSequence, Long>
{
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT s FROM LeaseSequence s WHERE s.rentalProfileId = :rentalProfileId AND s.year = :year")
  Optional<LeaseSequence> findForUpdate(Long rentalProfileId, int year);
}
