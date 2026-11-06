package tz.tante.rent.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.tante.rent.manager.enums.ViewingBookingStatus;
import tz.tante.rent.manager.models.entities.ViewingBooking;

import java.util.List;

@Repository
public interface ViewingBookingRepository extends JpaRepository<ViewingBooking, Long> {
  List<ViewingBooking> findByRentalProfileId(Long rentalProfileId);

  List<ViewingBooking> findByRequesterUserId(Long requesterUserId);

  List<ViewingBooking> findByStatus(ViewingBookingStatus status);
}
