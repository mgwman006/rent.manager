package tz.tante.rent.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.tante.rent.manager.enums.ViewingStatus;
import tz.tante.rent.manager.models.entities.ViewingRequest;

import java.util.List;

@Repository
public interface ViewingRequestRepository extends JpaRepository<ViewingRequest, Long> {
  List<ViewingRequest> findBySlotId(Long slotId);

  List<ViewingRequest> findBySlotRentalProfileId(Long rentalProfileId);

  List<ViewingRequest> findByRequesterUserId(Long requesterUserId);

  List<ViewingRequest> findByStatus(ViewingStatus status);
}
