package tz.tante.rent.manager.repositories;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import tz.tante.rent.manager.models.entities.RentalProfile;

import java.util.List;
import java.util.Optional;

public interface RentalProfileRepository extends JpaRepository<RentalProfile, Long>
{
  RentalProfile findByName(String name);

  Optional<RentalProfile> findByPhoneNumber(@NotNull(message = "Phone number is required") String s);

  List<RentalProfile> findByUserIdOrOrganizationId(Long userId, Long organizationId);
}
