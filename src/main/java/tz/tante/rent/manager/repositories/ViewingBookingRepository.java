package tz.tante.rent.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.tante.rent.manager.enums.ViewingBookingStatus;
import tz.tante.rent.manager.models.entities.ViewingBooking;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ViewingBookingRepository extends JpaRepository<ViewingBooking, Long> {
  List<ViewingBooking> findByRentalProfileId(Long rentalProfileId);

  List<ViewingBooking> findByRequesterUserAccountId(Long requesterUserAccountId);

  List<ViewingBooking> findByStatus(ViewingBookingStatus status);

  List<ViewingBooking> findByRentalProfileIdAndStatusNotIn(Long rentalProfileId, Collection<ViewingBookingStatus> excludedStatuses);

  List<ViewingBooking> findByStatusAndHoldExpiresAtBefore(ViewingBookingStatus status, LocalDateTime cutoff);

  Optional<ViewingBooking> findByReference(String reference);
}
