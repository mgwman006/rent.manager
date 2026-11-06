package tz.tante.rent.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.tante.rent.manager.models.entities.ViewingOutcome;

import java.util.Optional;

@Repository
public interface ViewingOutcomeRepository extends JpaRepository<ViewingOutcome, Long> {
  Optional<ViewingOutcome> findByRequestId(Long requestId);
}
