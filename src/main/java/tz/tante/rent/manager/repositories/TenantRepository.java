package tz.tante.rent.manager.repositories;

import tz.tante.rent.manager.models.entities.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantRepository extends JpaRepository<Tenant,Long> {

}
