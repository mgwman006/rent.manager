package tz.tante.rent.manager.repositories;

import tz.tante.rent.manager.models.entities.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant,Long> {


  @Query("SELECT t FROM Tenant t WHERE t.userId = :userId")
  Optional<Tenant> findByUserId(@Param("userId") Long userId);
}
