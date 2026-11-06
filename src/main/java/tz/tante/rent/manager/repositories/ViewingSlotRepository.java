package tz.tante.rent.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.tante.rent.manager.enums.ViewingSlotStatus;
import tz.tante.rent.manager.models.entities.ViewingSlot;

import java.util.List;

@Repository
public interface ViewingSlotRepository extends JpaRepository<ViewingSlot, Long> {
  List<ViewingSlot> findByRentalProfileId(Long rentalProfileId);

  List<ViewingSlot> findByRentalProfileIdAndStatus(Long rentalProfileId, ViewingSlotStatus status);

  List<ViewingSlot> findByPropertyId(Long propertyId);
}
