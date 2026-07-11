package tz.tante.rent.manager.repositories;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tz.tante.rent.manager.enums.MembershipRole;
import tz.tante.rent.manager.models.entities.Membership;

import java.util.List;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long>
{
  List<Membership> findByUserId(Long userId);
  @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM Membership m WHERE m.userId = :userId AND m.membershipRole = :membershipRole")
  boolean isExistByUserIdAndMembershipRole(@NotNull(message = "Admin user ID is required") Long userId, MembershipRole membershipRole);
}
